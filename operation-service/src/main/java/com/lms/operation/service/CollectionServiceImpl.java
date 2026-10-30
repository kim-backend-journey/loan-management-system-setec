package com.lms.operation.service;

import com.lms.operation.domain.CollectionLedger;
import com.lms.operation.domain.Customer;
import com.lms.operation.domain.DailyClosing;
import com.lms.operation.domain.Employee;
import com.lms.operation.domain.Loan;
import com.lms.operation.domain.LoanPayment;
import com.lms.operation.domain.LoanRequest;
import com.lms.operation.domain.PaymentTransaction;
import com.lms.operation.domain.User;
import com.lms.operation.dto.request.CloseDayRequest;
import com.lms.operation.dto.response.ChannelCollectionResponse;
import com.lms.operation.dto.response.DailyClosingResponse;
import com.lms.operation.dto.response.DailyCollectionResponse;
import com.lms.operation.dto.response.DailyPaymentItemResponse;
import com.lms.operation.dto.response.EmployeeSummaryResponse;
import com.lms.operation.exception.AppException;
import com.lms.operation.exception.ErrorCode;
import com.lms.operation.repository.CollectionLedgerRepository;
import com.lms.operation.repository.DailyClosingRepository;
import com.lms.operation.repository.EmployeeRepository;
import com.lms.operation.repository.LoanRequestRepository;
import com.lms.operation.repository.PaymentTransactionRepository;
import com.lms.operation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CollectionServiceImpl implements CollectionService {

    private static final String PAYMENT_METHOD_CASH = "CASH";
    private static final String PAYMENT_METHOD_BANK_TRANSFER = "BANK_TRANSFER";
    private static final String DEFAULT_CURRENCY = "USD";
    private static final String STATUS_CLOSED = "CLOSED";

    private final CollectionLedgerRepository collectionLedgerRepository;
    private final DailyClosingRepository dailyClosingRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final LoanRequestRepository loanRequestRepository;
    private final CurrentUserService currentUser;

    @Override
    @Transactional(readOnly = true)
    public DailyCollectionResponse getDailyEmployeeCollection(Integer employeeId, LocalDate date) {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_FOUND));

        List<CollectionLedger> ledgers = collectionLedgerRepository
                .findByEmployeeIdAndBusinessDateAndIsVoided(employeeId, date, false);

        List<CollectionLedger> cashLedgers = ledgers.stream()
                .filter(l -> PAYMENT_METHOD_CASH.equalsIgnoreCase(l.getPaymentMethod()))
                .toList();
        List<CollectionLedger> bankLedgers = ledgers.stream()
                .filter(l -> PAYMENT_METHOD_BANK_TRANSFER.equalsIgnoreCase(l.getPaymentMethod()))
                .toList();

        ChannelCollectionResponse cash = summarize(cashLedgers, null);
        ChannelCollectionResponse bankTransfer =
                summarize(bankLedgers, pendingVerificationAmount(bankLedgers));

        BigDecimal grandTotal = cash.total().add(bankTransfer.total());

        String currency = ledgers.stream()
                .map(CollectionLedger::getCurrency)
                .filter(c -> c != null && !c.isBlank())
                .findFirst()
                .orElse(DEFAULT_CURRENCY);

        EmployeeSummaryResponse employeeSummary = new EmployeeSummaryResponse(
                employee.getEmployeeId(), resolveEmployeeName(employee));

        return new DailyCollectionResponse(
                employeeSummary, date, currency, cash, bankTransfer, grandTotal);
    }

    @Override
    @Transactional
    public DailyClosingResponse closeDay(Integer employeeId, CloseDayRequest request) {

        LocalDate businessDate = request.getBusinessDate();
        if (businessDate.isAfter(LocalDate.now())) {
            throw new AppException(ErrorCode.VALIDATION_FAILED,
                    "Cannot close a business day in the future");
        }

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_FOUND));

        if (dailyClosingRepository.existsByEmployeeIdAndBusinessDate(employeeId, businessDate)) {
            throw new AppException(ErrorCode.DAY_ALREADY_CLOSED);
        }

        // Expected figures come from the same ledger the daily summary uses
        DailyCollectionResponse summary = getDailyEmployeeCollection(employeeId, businessDate);

        BigDecimal cashExpected = summary.cash().total();
        BigDecimal cashVariance = request.getCashCounted().subtract(cashExpected);

        if (cashVariance.signum() != 0
                && (request.getNotes() == null || request.getNotes().isBlank())) {
            throw new AppException(ErrorCode.VALIDATION_FAILED,
                    "Notes are required when counted cash does not match the ledger (variance "
                            + cashVariance.toPlainString() + ")");
        }

        DailyClosing closing = DailyClosing.builder()
                .employeeId(employeeId)
                .branchId(employee.getBranchId())
                .businessDate(businessDate)
                .currency(summary.currency())
                .transactionCount(summary.cash().transactionCount()
                        + summary.bankTransfer().transactionCount())
                .cashExpected(cashExpected)
                .cashCounted(request.getCashCounted())
                .cashVariance(cashVariance)
                .bankTransferTotal(summary.bankTransfer().total())
                .bankPendingVerification(summary.bankTransfer().pendingVerificationAmount())
                .grandTotal(summary.grandTotal())
                .status(STATUS_CLOSED)
                .notes(request.getNotes())
                .closedBy(getCurrentUserId())
                .closedAt(LocalDateTime.now())
                .build();
        dailyClosingRepository.save(closing);

        log.info("Business day closed: employee={} date={} expectedCash={} countedCash={} variance={}",
                employeeId, businessDate, cashExpected, request.getCashCounted(), cashVariance);

        return toClosingResponse(closing, summary.employee());
    }

    @Override
    @Transactional(readOnly = true)
    public DailyClosingResponse getDayClosing(Integer employeeId, LocalDate date) {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_FOUND));

        DailyClosing closing = dailyClosingRepository
                .findByEmployeeIdAndBusinessDate(employeeId, date)
                .orElseThrow(() -> new AppException(ErrorCode.DAY_CLOSING_NOT_FOUND));

        return toClosingResponse(closing, new EmployeeSummaryResponse(
                employee.getEmployeeId(), resolveEmployeeName(employee)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DailyPaymentItemResponse> getDailyPayments(LocalDate date, Integer employeeId) {
        // Officers only see payments they received; admins see everyone's, or one officer's.
        Integer receivedBy;
        if (currentUser.isAdmin()) {
            receivedBy = employeeId == null ? null : employeeRepository.findById(employeeId)
                    .map(Employee::getUserId)
                    .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_FOUND));
        } else {
            receivedBy = currentUser.userId();
            if (receivedBy == null) {
                return List.of();
            }
        }

        List<PaymentTransaction> transactions = paymentTransactionRepository.findReceivedBetween(
                date.atStartOfDay(), date.plusDays(1).atStartOfDay(), receivedBy);

        Map<Integer, String> receiverNames = new HashMap<>();
        return transactions.stream()
                .map(transaction -> toDailyPaymentItem(transaction, receiverNames))
                .toList();
    }

    private DailyPaymentItemResponse toDailyPaymentItem(PaymentTransaction transaction,
                                                        Map<Integer, String> receiverNames) {
        LoanPayment installment = transaction.getPayment();
        Loan loan = installment.getLoan();
        Customer customer = resolveCustomer(loan);
        CollectionLedger ledger = collectionLedgerRepository
                .findByTransactionId(transaction.getTransactionId())
                .orElse(null);
        Integer receiverId = transaction.getReceivedBy();
        String receiverName = receiverId == null ? null
                : receiverNames.computeIfAbsent(receiverId, this::userName);

        return new DailyPaymentItemResponse(
                transaction.getTransactionId(),
                transaction.getReceiptNumber(),
                transaction.getTransactionDate(),
                transaction.getAmount(),
                transaction.getCurrency() != null ? transaction.getCurrency() : loan.getCurrency(),
                transaction.getPaymentMethod() == null ? null : transaction.getPaymentMethod().toUpperCase(),
                transaction.getBankVerificationStatus(),
                loan.getLoanId(),
                loan.getLoanNumber(),
                installment.getPaymentId(),
                installment.getPaymentNo(),
                installment.getPaymentStatus() == null ? null : installment.getPaymentStatus().toUpperCase(),
                customer == null ? null : customer.getCustomerId(),
                customer == null ? null : customer.getFirstName() + " " + customer.getLastName(),
                receiverId,
                receiverName,
                ledger == null ? null : ledger.getPrincipalAmount(),
                ledger == null ? null : ledger.getInterestAmount(),
                ledger == null ? null : ledger.getPenaltyAmount()
        );
    }

    private Customer resolveCustomer(Loan loan) {
        if (loan.getApplication() != null && loan.getApplication().getCustomer() != null) {
            return loan.getApplication().getCustomer();
        }
        if (loan.getLoanRequestId() != null) {
            return loanRequestRepository.findById(loan.getLoanRequestId())
                    .map(LoanRequest::getCustomer)
                    .orElse(null);
        }
        return null;
    }

    private String userName(Integer userId) {
        return userRepository.findById(userId)
                .map(user -> ((user.getFirstName() == null ? "" : user.getFirstName()) + " "
                        + (user.getLastName() == null ? "" : user.getLastName())).trim())
                .filter(name -> !name.isBlank())
                .orElse("#" + userId);
    }

    private DailyClosingResponse toClosingResponse(DailyClosing closing,
                                                   EmployeeSummaryResponse employee) {
        return new DailyClosingResponse(
                closing.getClosingId(),
                employee,
                closing.getBranchId(),
                closing.getBusinessDate(),
                closing.getCurrency(),
                closing.getTransactionCount(),
                closing.getCashExpected(),
                closing.getCashCounted(),
                closing.getCashVariance(),
                closing.getBankTransferTotal(),
                closing.getBankPendingVerification(),
                closing.getGrandTotal(),
                closing.getStatus(),
                closing.getNotes(),
                closing.getClosedBy(),
                closing.getClosedAt()
        );
    }

    private Integer getCurrentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof UsernamePasswordAuthenticationToken token
                && token.getDetails() instanceof Integer userId) {
            return userId;
        }
        return null;
    }

    private ChannelCollectionResponse summarize(List<CollectionLedger> ledgers,
                                                BigDecimal pendingVerificationAmount) {
        return new ChannelCollectionResponse(
                ledgers.size(),
                sum(ledgers, CollectionLedger::getPrincipalAmount),
                sum(ledgers, CollectionLedger::getInterestAmount),
                sum(ledgers, CollectionLedger::getServiceFeeAmount),
                sum(ledgers, CollectionLedger::getPenaltyAmount),
                sum(ledgers, CollectionLedger::getTotalAmount),
                pendingVerificationAmount
        );
    }

    private BigDecimal pendingVerificationAmount(List<CollectionLedger> bankLedgers) {
        List<Integer> transactionIds = bankLedgers.stream()
                .map(CollectionLedger::getTransactionId)
                .filter(id -> id != null)
                .toList();
        if (transactionIds.isEmpty()) {
            return BigDecimal.ZERO;
        }

        Map<Integer, String> statusByTxnId = paymentTransactionRepository
                .findAllById(transactionIds).stream()
                .collect(Collectors.toMap(
                        PaymentTransaction::getTransactionId,
                        t -> t.getBankVerificationStatus() == null ? "" : t.getBankVerificationStatus()));

        return bankLedgers.stream()
                .filter(l -> l.getTransactionId() != null
                        && "PENDING".equalsIgnoreCase(statusByTxnId.get(l.getTransactionId())))
                .map(CollectionLedger::getTotalAmount)
                .filter(a -> a != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal sum(List<CollectionLedger> ledgers,
                           Function<CollectionLedger, BigDecimal> getter) {
        return ledgers.stream()
                .map(getter)
                .filter(a -> a != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String resolveEmployeeName(Employee employee) {
        if (employee.getUserId() != null) {
            User user = userRepository.findById(employee.getUserId()).orElse(null);
            if (user != null) {
                String name = ((user.getFirstName() == null ? "" : user.getFirstName())
                        + " " + (user.getLastName() == null ? "" : user.getLastName())).trim();
                if (!name.isBlank()) {
                    return name;
                }
            }
        }
        if (employee.getPosition() != null && !employee.getPosition().isBlank()) {
            return employee.getPosition();
        }
        return employee.getEmployeeCode();
    }
}

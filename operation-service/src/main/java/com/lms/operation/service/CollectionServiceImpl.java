package com.lms.operation.service;

import com.lms.operation.domain.CollectionLedger;
import com.lms.operation.domain.Employee;
import com.lms.operation.domain.PaymentTransaction;
import com.lms.operation.domain.User;
import com.lms.operation.dto.response.ChannelCollectionResponse;
import com.lms.operation.dto.response.DailyCollectionResponse;
import com.lms.operation.dto.response.EmployeeSummaryResponse;
import com.lms.operation.exception.AppException;
import com.lms.operation.exception.ErrorCode;
import com.lms.operation.repository.CollectionLedgerRepository;
import com.lms.operation.repository.EmployeeRepository;
import com.lms.operation.repository.PaymentTransactionRepository;
import com.lms.operation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    private static final String DEFAULT_CURRENCY = "KHR";

    private final CollectionLedgerRepository collectionLedgerRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;

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

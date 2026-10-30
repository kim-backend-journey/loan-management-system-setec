package com.lms.operation.service;

import com.lms.operation.domain.Customer;
import com.lms.operation.domain.Employee;
import com.lms.operation.domain.Loan;
import com.lms.operation.domain.LoanApplication;
import com.lms.operation.domain.LoanPayment;
import com.lms.operation.domain.LoanRequest;
import com.lms.operation.dto.response.DueInstallmentResponse;
import com.lms.operation.dto.response.LoanScheduleDetailResponse;
import com.lms.operation.dto.response.LoanScheduleItemResponse;
import com.lms.operation.dto.response.PaginatedDueInstallmentResponse;
import com.lms.operation.dto.response.PaginatedLoanScheduleResponse;
import com.lms.operation.dto.response.SchedulePaymentItemResponse;
import com.lms.operation.exception.AppException;
import com.lms.operation.exception.ErrorCode;
import com.lms.operation.repository.EmployeeRepository;
import com.lms.operation.repository.LoanPaymentRepository;
import com.lms.operation.repository.LoanRepository;
import com.lms.operation.repository.LoanRequestRepository;
import com.lms.operation.repository.UserRepository;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanScheduleServiceImpl implements LoanScheduleService {

    // Lower bound for "overdue": any unpaid installment due before today.
    private static final LocalDate EARLIEST_DUE_DATE = LocalDate.of(1900, 1, 1);

    private final LoanRepository loanRepository;
    private final LoanPaymentRepository loanPaymentRepository;
    private final EmployeeRepository employeeRepository;
    private final LoanRequestRepository loanRequestRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUser;

    @Override
    @Transactional(readOnly = true)
    public PaginatedLoanScheduleResponse getLoanSchedules(String status,
                                                          LocalDate fromDate,
                                                          LocalDate toDate,
                                                          Integer employeeId,
                                                          Integer branchId,
                                                          String search,
                                                          Integer page,
                                                          Integer size) {

        int pageNumber = (page == null || page < 1) ? 1 : page;
        int pageSize = (size == null || size < 1) ? 20 : size;

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize,
                Sort.by(Sort.Direction.DESC, "loanId"));

        Specification<Loan> spec = buildSpecification(status, fromDate, toDate,
                employeeId, branchId, search);

        Page<Loan> loanPage = loanRepository.findAll(spec, pageable);

        List<LoanScheduleItemResponse> items = new ArrayList<>();
        for (Loan loan : loanPage.getContent()) {
            List<LoanPayment> payments =
                    loanPaymentRepository.findByLoan_LoanId(loan.getLoanId());
            items.add(buildItem(loan, payments));
        }

        return new PaginatedLoanScheduleResponse(
                items,
                pageNumber,
                pageSize,
                loanPage.getTotalElements(),
                loanPage.getTotalPages()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public LoanScheduleDetailResponse getLoanScheduleDetail(Integer loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new AppException(ErrorCode.LOAN_NOT_FOUND));

        List<LoanPayment> payments =
                loanPaymentRepository.findByLoan_LoanId(loan.getLoanId());

        LoanScheduleItemResponse item = buildItem(loan, payments);
        List<SchedulePaymentItemResponse> paymentItems = payments.stream()
                .sorted(Comparator.comparing(LoanPayment::getPaymentNo,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .map(this::toPaymentItem)
                .toList();

        return new LoanScheduleDetailResponse(
                item.scheduleId(),
                item.loanId(),
                item.loanNumber(),
                item.customerId(),
                item.customerName(),
                item.customerPhone(),
                item.employeeId(),
                item.employeeName(),
                item.principalAmount(),
                item.currency(),
                item.duration(),
                item.repaymentFrequency(),
                item.startDate(),
                item.endDate(),
                item.installmentAmount(),
                item.totalSchedules(),
                item.paidSchedules(),
                item.remainingSchedules(),
                item.totalPayableAmount(),
                item.totalPaidAmount(),
                item.totalRemainingAmount(),
                item.progressPercentage(),
                item.status(),
                paymentItems
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedDueInstallmentResponse getDueToday(Integer employeeId, Integer page, Integer size) {
        LocalDate today = LocalDate.now();
        return dueInstallments(today, today, employeeId, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedDueInstallmentResponse getOverdue(Integer employeeId, Integer page, Integer size) {
        return dueInstallments(EARLIEST_DUE_DATE, LocalDate.now().minusDays(1), employeeId, page, size);
    }

    /**
     * Officers only ever see loans assigned to their own employee record (the employeeId parameter is
     * ignored for them); a user with no employee record sees nothing. Admins see every loan, or one
     * officer's when employeeId is given.
     */
    private PaginatedDueInstallmentResponse dueInstallments(LocalDate from, LocalDate to, Integer employeeId,
                                                            Integer page, Integer size) {
        int pageNumber = (page == null || page < 1) ? 1 : page;
        int pageSize = (size == null || size < 1) ? 20 : Math.min(size, 100);

        Integer scopeEmployeeId = employeeId;
        if (!currentUser.isAdmin()) {
            Optional<Employee> me = currentUser.employee();
            if (me.isEmpty()) {
                return new PaginatedDueInstallmentResponse(List.of(), pageNumber, pageSize, 0, 0);
            }
            scopeEmployeeId = me.get().getEmployeeId();
        }

        Page<LoanPayment> result = loanPaymentRepository.findOpenInstallmentsDueBetween(
                from, to, scopeEmployeeId, PageRequest.of(pageNumber - 1, pageSize));

        LocalDate today = LocalDate.now();
        List<DueInstallmentResponse> items = result.getContent().stream()
                .map(payment -> toDueItem(payment, today))
                .toList();

        return new PaginatedDueInstallmentResponse(
                items, pageNumber, pageSize, result.getTotalElements(), result.getTotalPages());
    }

    private DueInstallmentResponse toDueItem(LoanPayment payment, LocalDate today) {
        Loan loan = payment.getLoan();
        Customer customer = resolveCustomer(loan);
        BigDecimal outstanding = nz(payment.getAmountDue())
                .add(nz(payment.getPenaltyAmount()))
                .add(nz(payment.getLateFee()))
                .subtract(nz(payment.getAmountPaid()))
                .max(BigDecimal.ZERO);
        long daysOverdue = payment.getDueDate() == null
                ? 0 : Math.max(0, ChronoUnit.DAYS.between(payment.getDueDate(), today));

        return new DueInstallmentResponse(
                payment.getPaymentId(),
                payment.getPaymentNo(),
                payment.getDueDate(),
                daysOverdue,
                payment.getAmountDue(),
                nz(payment.getAmountPaid()),
                nz(payment.getPenaltyAmount()),
                nz(payment.getLateFee()),
                outstanding,
                payment.getPaymentStatus() == null ? null : payment.getPaymentStatus().toUpperCase(),
                loan.getLoanId(),
                loan.getLoanNumber(),
                loan.getCurrency() == null ? "USD" : loan.getCurrency(),
                customer == null ? null : customer.getCustomerId(),
                customer == null ? null : customer.getFirstName() + " " + customer.getLastName(),
                customer == null ? null : customer.getPhoneNumber(),
                loan.getEmployeeId(),
                officerName(loan.getEmployeeId())
        );
    }

    /** The officer's own name (from their user account), falling back to the employee code. */
    private String officerName(Integer employeeId) {
        if (employeeId == null) {
            return null;
        }
        return employeeRepository.findById(employeeId)
                .map(employee -> employee.getUserId() == null ? null
                        : userRepository.findById(employee.getUserId())
                        .map(user -> ((user.getFirstName() == null ? "" : user.getFirstName()) + " "
                                + (user.getLastName() == null ? "" : user.getLastName())).trim())
                        .filter(name -> !name.isBlank())
                        .orElse(employee.getEmployeeCode()))
                .orElse(null);
    }

    private BigDecimal nz(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private Specification<Loan> buildSpecification(String status,
                                                   LocalDate fromDate,
                                                   LocalDate toDate,
                                                   Integer employeeId,
                                                   Integer branchId,
                                                   String search) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (status != null && !status.isBlank()) {
                predicates.add(cb.equal(cb.upper(root.get("loanStatus")),
                        status.trim().toUpperCase()));
            }
            if (fromDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("startDate"), fromDate));
            }
            if (toDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("endDate"), toDate));
            }
            if (employeeId != null) {
                predicates.add(cb.equal(root.get("employeeId"), employeeId));
            }
            if (branchId != null) {
                predicates.add(cb.equal(root.get("branchId"), branchId));
            }
            if (search != null && !search.isBlank()) {
                String like = "%" + search.trim().toLowerCase() + "%";

                // customer is reachable through the (optional) application -> customer path
                Join<Loan, LoanApplication> appJoin = root.join("application", JoinType.LEFT);
                Join<LoanApplication, Customer> customerJoin = appJoin.join("customer", JoinType.LEFT);
                Expression<String> fullName = cb.lower(
                        cb.concat(cb.concat(customerJoin.get("firstName"), " "),
                                customerJoin.get("lastName")));

                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("loanNumber")), like),
                        cb.like(fullName, like)
                ));
                query.distinct(true);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private LoanScheduleItemResponse buildItem(Loan loan, List<LoanPayment> payments) {
        Customer customer = resolveCustomer(loan);
        String customerName = customer == null ? null
                : customer.getFirstName() + " " + customer.getLastName();
        String customerPhone = customer == null ? null : customer.getPhoneNumber();
        Integer customerId = customer == null ? null : customer.getCustomerId();

        String employeeName = loan.getEmployeeId() == null ? "N/A" : officerName(loan.getEmployeeId());

        int totalSchedules = payments.size();
        int paidSchedules = (int) payments.stream()
                .filter(p -> "Paid".equalsIgnoreCase(p.getPaymentStatus()))
                .count();
        int remainingSchedules = totalSchedules - paidSchedules;

        BigDecimal installmentAmount = loan.getMonthlyPayment();
        BigDecimal totalPayableAmount = (installmentAmount == null || loan.getLoanTermMonths() == null)
                ? BigDecimal.ZERO
                : installmentAmount.multiply(BigDecimal.valueOf(loan.getLoanTermMonths()));
        BigDecimal totalPaidAmount = payments.stream()
                .map(LoanPayment::getAmountPaid)
                .filter(a -> a != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalRemainingAmount = loan.getOutstandingBalance();

        double progressPercentage = totalSchedules == 0 ? 0.0
                : BigDecimal.valueOf(paidSchedules * 100.0 / totalSchedules)
                .setScale(2, RoundingMode.HALF_UP).doubleValue();

        String currency = loan.getCurrency() == null ? "USD" : loan.getCurrency();
        String repaymentFrequency = loan.getRepaymentFrequency() == null
                ? "MONTHLY" : loan.getRepaymentFrequency();
        String status = loan.getLoanStatus() == null ? null : loan.getLoanStatus().toUpperCase();

        return new LoanScheduleItemResponse(
                loan.getLoanId(),           // scheduleId (per-loan schedule identifier)
                loan.getLoanId(),
                loan.getLoanNumber(),
                customerId,
                customerName,
                customerPhone,
                loan.getEmployeeId(),
                employeeName,
                loan.getApprovedAmount(),
                currency,
                loan.getLoanTermMonths(),
                repaymentFrequency,
                loan.getStartDate(),
                loan.getEndDate(),
                installmentAmount,
                totalSchedules,
                paidSchedules,
                remainingSchedules,
                totalPayableAmount,
                totalPaidAmount,
                totalRemainingAmount,
                progressPercentage,
                status,
                loan.getApplication() == null ? null : loan.getApplication().getApplicationId()
        );
    }

    private SchedulePaymentItemResponse toPaymentItem(LoanPayment p) {
        return new SchedulePaymentItemResponse(
                p.getPaymentId(),
                p.getPaymentNo(),
                p.getDueDate(),
                p.getAmountDue(),
                p.getAmountPaid(),
                p.getRemainingBalance(),
                p.getPaymentStatus() == null ? null : p.getPaymentStatus().toUpperCase(),
                p.getPaymentDate(),
                p.getLateFee(),
                p.getPenaltyAmount()
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
}

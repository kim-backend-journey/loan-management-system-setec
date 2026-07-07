package com.lms.operation.service;

import com.lms.operation.domain.Customer;
import com.lms.operation.domain.EmploymentHistory;
import com.lms.operation.dto.request.CreateCustomerRequest;
import com.lms.operation.dto.request.EmploymentRequest;
import com.lms.operation.dto.request.UpdateCustomerRequest;
import com.lms.operation.dto.response.CustomerResponse;
import com.lms.operation.dto.response.EmploymentResponse;
import com.lms.operation.exception.AppException;
import com.lms.operation.exception.ErrorCode;
import com.lms.operation.repository.CustomerRepository;
import com.lms.operation.repository.EmploymentHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final EmploymentHistoryRepository employmentHistoryRepository;

    @Override
    @Transactional
    public CustomerResponse createCustomer(CreateCustomerRequest request) {
        if (customerRepository.existsByNationalId(request.getNationalId())) {
            throw new AppException(ErrorCode.CUSTOMER_ALREADY_EXISTS,
                    "National ID already registered");
        }
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.CUSTOMER_ALREADY_EXISTS,
                    "Email already registered");
        }
        if (customerRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new AppException(ErrorCode.CUSTOMER_ALREADY_EXISTS,
                    "Phone number already registered");
        }

        Customer customer = Customer.builder()
                .userId(request.getUserId())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .nationalId(request.getNationalId())
                .address(request.getAddress())
                .status("Active")
                .build();

        customerRepository.save(customer);
        log.info("Customer created: {} {}",
                customer.getFirstName(), customer.getLastName());
        return toResponse(customer);
    }

    @Override
    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public CustomerResponse getCustomerById(Integer customerId) {
        return toResponse(findCustomer(customerId));
    }

    @Override
    @Transactional
    public CustomerResponse updateCustomer(Integer customerId,
                                           UpdateCustomerRequest request) {
        Customer customer = findCustomer(customerId);

        if (request.getFirstName() != null)
            customer.setFirstName(request.getFirstName());
        if (request.getLastName() != null)
            customer.setLastName(request.getLastName());
        if (request.getDateOfBirth() != null)
            customer.setDateOfBirth(request.getDateOfBirth());
        if (request.getGender() != null)
            customer.setGender(request.getGender());
        if (request.getPhoneNumber() != null)
            customer.setPhoneNumber(request.getPhoneNumber());
        if (request.getEmail() != null)
            customer.setEmail(request.getEmail());
        if (request.getAddress() != null)
            customer.setAddress(request.getAddress());
        if (request.getProfileImage() != null)
            customer.setProfileImage(request.getProfileImage());

        customerRepository.save(customer);
        return toResponse(customer);
    }

    @Override
    public List<EmploymentResponse> getEmploymentHistory(Integer customerId) {
        findCustomer(customerId);
        return employmentHistoryRepository
                .findByCustomer_CustomerId(customerId)
                .stream()
                .map(this::toEmploymentResponse)
                .toList();
    }

    @Override
    @Transactional
    public EmploymentResponse addEmployment(Integer customerId,
                                            EmploymentRequest request) {
        Customer customer = findCustomer(customerId);

        EmploymentHistory employment = EmploymentHistory.builder()
                .customer(customer)
                .employerName(request.getEmployerName())
                .occupation(request.getOccupation())
                .monthlyIncome(request.getMonthlyIncome())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .isCurrent(request.getIsCurrent() != null
                        ? request.getIsCurrent() : false)
                .build();

        employmentHistoryRepository.save(employment);
        log.info("Employment added for customer: {}", customerId);
        return toEmploymentResponse(employment);
    }

    @Override
    @Transactional
    public EmploymentResponse updateEmployment(Integer customerId,
                                               Integer employmentId,
                                               EmploymentRequest request) {
        findCustomer(customerId);

        EmploymentHistory employment = employmentHistoryRepository
                .findById(employmentId)
                .orElseThrow(() -> new AppException(
                        ErrorCode.INTERNAL_ERROR, "Employment record not found"));

        if (request.getEmployerName() != null)
            employment.setEmployerName(request.getEmployerName());
        if (request.getOccupation() != null)
            employment.setOccupation(request.getOccupation());
        if (request.getMonthlyIncome() != null)
            employment.setMonthlyIncome(request.getMonthlyIncome());
        if (request.getStartDate() != null)
            employment.setStartDate(request.getStartDate());
        if (request.getEndDate() != null)
            employment.setEndDate(request.getEndDate());
        if (request.getIsCurrent() != null)
            employment.setIsCurrent(request.getIsCurrent());

        employmentHistoryRepository.save(employment);
        return toEmploymentResponse(employment);
    }

    private Customer findCustomer(Integer customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new AppException(
                        ErrorCode.CUSTOMER_NOT_FOUND));
    }

    private CustomerResponse toResponse(Customer c) {
        return new CustomerResponse(
                c.getCustomerId(),
                c.getUserId(),
                c.getFirstName(),
                c.getLastName(),
                c.getDateOfBirth(),
                c.getGender(),
                c.getPhoneNumber(),
                c.getEmail(),
                c.getNationalId(),
                c.getAddress(),
                c.getProfileImage(),
                c.getStatus(),
                c.getCreatedAt(),
                c.getUpdatedAt()
        );
    }

    private EmploymentResponse toEmploymentResponse(EmploymentHistory e) {
        return new EmploymentResponse(
                e.getEmploymentId(),
                e.getCustomer().getCustomerId(),
                e.getEmployerName(),
                e.getOccupation(),
                e.getMonthlyIncome(),
                e.getStartDate(),
                e.getEndDate(),
                e.getIsCurrent(),
                e.getCreatedAt()
        );
    }
}
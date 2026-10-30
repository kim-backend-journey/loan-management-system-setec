CREATE TABLE IF NOT EXISTS roles (
    role_id     SERIAL PRIMARY KEY,
    role_name   VARCHAR(50) UNIQUE NOT NULL,
    description TEXT
);

CREATE TABLE IF NOT EXISTS users (
    user_id        SERIAL PRIMARY KEY,
    role_id        INT REFERENCES roles(role_id),
    username       VARCHAR(100) UNIQUE NOT NULL,
    email          VARCHAR(150) UNIQUE NOT NULL,
    phone_number   VARCHAR(20) UNIQUE,
    password       VARCHAR(255) NOT NULL,
    first_name     VARCHAR(100),
    last_name      VARCHAR(100),
    status         VARCHAR(20) DEFAULT 'Active',
    email_verified BOOLEAN DEFAULT FALSE,
    phone_verified BOOLEAN DEFAULT FALSE,
    last_login_at  TIMESTAMP,
    created_at     TIMESTAMP DEFAULT NOW(),
    updated_at     TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS refresh_tokens (
    refresh_token_id SERIAL PRIMARY KEY,
    user_id          INT NOT NULL REFERENCES users(user_id),
    token            TEXT NOT NULL,
    expires_at       TIMESTAMP,
    revoked          BOOLEAN DEFAULT FALSE,
    created_at       TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS password_reset_tokens (
    reset_token_id SERIAL PRIMARY KEY,
    user_id        INT NOT NULL REFERENCES users(user_id),
    token          TEXT NOT NULL,
    expires_at     TIMESTAMP,
    used           BOOLEAN DEFAULT FALSE,
    created_at     TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS login_history (
    login_history_id SERIAL PRIMARY KEY,
    user_id          INT NOT NULL REFERENCES users(user_id),
    login_identifier VARCHAR(150),
    login_method     VARCHAR(50),
    ip_address       VARCHAR(50),
    user_agent       TEXT,
    login_status     VARCHAR(20),
    login_time       TIMESTAMP,
    logout_time      TIMESTAMP,
    created_at       TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS customers (
    customer_id    SERIAL PRIMARY KEY,
    user_id        INT UNIQUE REFERENCES users(user_id),
    first_name     VARCHAR(100) NOT NULL,
    last_name      VARCHAR(100) NOT NULL,
    date_of_birth  DATE,
    gender         VARCHAR(10),
    phone_number   VARCHAR(20) UNIQUE,
    email          VARCHAR(150) UNIQUE,
    national_id    VARCHAR(50) UNIQUE,
    address        TEXT,
    profile_image  VARCHAR(255),
    status         VARCHAR(20) DEFAULT 'Active',
    created_at     TIMESTAMP DEFAULT NOW(),
    updated_at     TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS employment_history (
    employment_id  SERIAL PRIMARY KEY,
    customer_id    INT NOT NULL REFERENCES customers(customer_id),
    employer_name  VARCHAR(255),
    occupation     VARCHAR(100),
    monthly_income DECIMAL(15,2),
    start_date     DATE,
    end_date       DATE,
    is_current     BOOLEAN DEFAULT FALSE,
    created_at     TIMESTAMP DEFAULT NOW(),
    updated_at     TIMESTAMP DEFAULT NOW()
);


CREATE TABLE IF NOT EXISTS loan_products (
    loan_product_id     SERIAL PRIMARY KEY,
    product_name        VARCHAR(255) NOT NULL,
    max_loan_amount     DECIMAL(15,2),
    minimum_income      DECIMAL(15,2),
    interest_rate       DECIMAL(5,2),
    minimum_term_months INT,
    maximum_term_months INT,
    processing_fee      DECIMAL(15,2),
    description         TEXT,
    status              VARCHAR(20) DEFAULT 'Active',
    reviewed_by         INT REFERENCES users(user_id),
    reviewed_at         TIMESTAMP,
    created_at          TIMESTAMP DEFAULT NOW(),
    updated_at          TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS loan_applications (
    application_id        SERIAL PRIMARY KEY,
    application_number    VARCHAR(50) UNIQUE NOT NULL,
    customer_id           INT NOT NULL REFERENCES customers(customer_id),
    loan_product_id       INT NOT NULL REFERENCES loan_products(loan_product_id),
    requested_amount      DECIMAL(15,2),
    requested_term_months INT,
    loan_purpose          TEXT,
    application_status    VARCHAR(30) DEFAULT 'Draft',
    submitted_at          TIMESTAMP,
    created_at            TIMESTAMP DEFAULT NOW(),
    updated_at            TIMESTAMP DEFAULT NOW()
);


CREATE TABLE IF NOT EXISTS documents (
    document_id         SERIAL PRIMARY KEY,
    application_id      INT NOT NULL REFERENCES loan_applications(application_id),
    document_type       VARCHAR(100),
    file_path           VARCHAR(500),
    verification_status VARCHAR(20) DEFAULT 'Pending',
    verified_by         INT REFERENCES users(user_id),
    verification_comment TEXT,
    verified_at         TIMESTAMP,
    original_file_name  VARCHAR(255),
    mime_type           VARCHAR(100),
    file_size           BIGINT,
    created_at          TIMESTAMP DEFAULT NOW(),
    updated_at          TIMESTAMP DEFAULT NOW()
);


CREATE TABLE IF NOT EXISTS risk_assessments (
    assessment_id      SERIAL PRIMARY KEY,
    application_id     INT NOT NULL REFERENCES loan_applications(application_id),
    risk_score         DECIMAL(5,2),
    risk_level         VARCHAR(20),
    assessment_result  VARCHAR(30),
    assessment_comment TEXT,
    assessed_by        INT REFERENCES users(user_id),
    assessed_at        TIMESTAMP,
    created_at         TIMESTAMP DEFAULT NOW(),
    updated_at         TIMESTAMP DEFAULT NOW()
);


CREATE TABLE IF NOT EXISTS guarantors (
    guarantor_id             SERIAL PRIMARY KEY,
    application_id           INT NOT NULL REFERENCES loan_applications(application_id),
    first_name               VARCHAR(100),
    last_name                VARCHAR(100),
    date_of_birth            DATE,
    phone_number             VARCHAR(20),
    email                    VARCHAR(150) UNIQUE,
    national_id              VARCHAR(50) UNIQUE,
    address                  TEXT,
    occupation               VARCHAR(100),
    employer                 VARCHAR(255),
    monthly_income           DECIMAL(15,2),
    relationship_to_customer VARCHAR(100),
    verification_status      VARCHAR(20) DEFAULT 'Pending',
    verified_by              INT REFERENCES users(user_id),
    verified_at              TIMESTAMP,
    created_at                TIMESTAMP DEFAULT NOW(),
    updated_at                TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS collaterals (
    collateral_id       SERIAL PRIMARY KEY,
    application_id      INT NOT NULL REFERENCES loan_applications(application_id),
    collateral_type     VARCHAR(100),
    description         TEXT,
    estimated_value     DECIMAL(15,2),
    owner_name          VARCHAR(255),
    owner_national_id   VARCHAR(50),
    verification_status VARCHAR(20) DEFAULT 'Pending',
    verified_by         INT REFERENCES users(user_id),
    verified_at         TIMESTAMP,
    created_at          TIMESTAMP DEFAULT NOW(),
    updated_at          TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS loan_approvals (
    approval_id      SERIAL PRIMARY KEY,
    application_id   INT NOT NULL REFERENCES loan_applications(application_id),
    approved_by      INT NOT NULL REFERENCES users(user_id),
    approval_status  VARCHAR(20) DEFAULT 'Pending',
    decision_reason  TEXT,
    approved_at      TIMESTAMP,
    created_at       TIMESTAMP DEFAULT NOW(),
    updated_at       TIMESTAMP DEFAULT NOW()
);


CREATE TABLE IF NOT EXISTS loans (
    loan_id             SERIAL PRIMARY KEY,
    application_id      INT UNIQUE NOT NULL REFERENCES loan_applications(application_id),
    loan_number         VARCHAR(50) UNIQUE NOT NULL,
    approved_amount     DECIMAL(15,2),
    interest_rate       DECIMAL(5,2),
    loan_term_months    INT,
    monthly_payment     DECIMAL(15,2),
    outstanding_balance DECIMAL(15,2),
    loan_status         VARCHAR(20) DEFAULT 'Active',
    start_date          DATE,
    end_date            DATE,
    created_at          TIMESTAMP DEFAULT NOW(),
    updated_at          TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS loan_payments (
    payment_id        SERIAL PRIMARY KEY,
    loan_id           INT NOT NULL REFERENCES loans(loan_id),
    payment_no        INT,
    due_date          DATE,
    amount_due        DECIMAL(15,2),
    amount_paid       DECIMAL(15,2) DEFAULT 0,
    remaining_balance DECIMAL(15,2),
    payment_date      TIMESTAMP,
    payment_status    VARCHAR(20) DEFAULT 'Pending',
    paid_by           VARCHAR(50),
    late_fee          DECIMAL(15,2) DEFAULT 0,
    penalty_amount    DECIMAL(15,2) DEFAULT 0,
    created_at        TIMESTAMP DEFAULT NOW(),
    updated_at        TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS payment_transactions (
    transaction_id     SERIAL PRIMARY KEY,
    payment_id         INT NOT NULL REFERENCES loan_payments(payment_id),
    payment_method     VARCHAR(50),
    amount             DECIMAL(15,2),
    reference_number   VARCHAR(100),
    transaction_status VARCHAR(20) DEFAULT 'Pending',
    received_by        INT NOT NULL REFERENCES users(user_id),
    transaction_date   TIMESTAMP,
    created_at         TIMESTAMP DEFAULT NOW(),
    updated_at         TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS notifications (
    notification_id     SERIAL PRIMARY KEY,
    customer_id         INT NOT NULL REFERENCES customers(customer_id),
    created_by          INT NOT NULL REFERENCES users(user_id),
    notification_type   VARCHAR(50),
    title                VARCHAR(255),
    message              TEXT,
    notification_status  VARCHAR(20) DEFAULT 'Queued',
    channel              VARCHAR(20) DEFAULT 'InApp',
    sent_at              TIMESTAMP,
    is_read              BOOLEAN DEFAULT FALSE,
    read_at              TIMESTAMP,
    created_at           TIMESTAMP DEFAULT NOW(),
    updated_at           TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS application_status_history (
    history_id     SERIAL PRIMARY KEY,
    application_id INT NOT NULL REFERENCES loan_applications(application_id),
    changed_by     INT NOT NULL REFERENCES users(user_id),
    old_status     VARCHAR(30),
    new_status     VARCHAR(30),
    remark         TEXT,
    created_at     TIMESTAMP DEFAULT NOW(),
    updated_at     TIMESTAMP DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_users_email            ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_phone            ON users(phone_number);
CREATE INDEX IF NOT EXISTS idx_users_username         ON users(username);

CREATE INDEX IF NOT EXISTS idx_customers_email        ON customers(email);
CREATE INDEX IF NOT EXISTS idx_customers_phone        ON customers(phone_number);
CREATE INDEX IF NOT EXISTS idx_customers_national_id  ON customers(national_id);

CREATE INDEX IF NOT EXISTS idx_applications_customer  ON loan_applications(customer_id);
CREATE INDEX IF NOT EXISTS idx_applications_status    ON loan_applications(application_status);
CREATE INDEX IF NOT EXISTS idx_applications_product   ON loan_applications(loan_product_id);

CREATE INDEX IF NOT EXISTS idx_loans_number           ON loans(loan_number);
CREATE INDEX IF NOT EXISTS idx_loans_status           ON loans(loan_status);

CREATE INDEX IF NOT EXISTS idx_payments_loan          ON loan_payments(loan_id);
CREATE INDEX IF NOT EXISTS idx_payments_status        ON loan_payments(payment_status);
CREATE INDEX IF NOT EXISTS idx_payments_due           ON loan_payments(due_date);

CREATE INDEX IF NOT EXISTS idx_transactions_ref       ON payment_transactions(reference_number);

CREATE INDEX IF NOT EXISTS idx_notifications_customer ON notifications(customer_id);
CREATE INDEX IF NOT EXISTS idx_notifications_read     ON notifications(is_read);


-- ============================================================
-- Loan request, disbursement, repayment and collection flow
-- Matches the operation-service entities. Written to be re-runnable
-- (IF NOT EXISTS), so an existing database can be upgraded with:
--   docker exec -i lms-postgres psql -U <user> -d <db> < init.sql
-- ============================================================

CREATE TABLE IF NOT EXISTS branches (
    branch_id   SERIAL PRIMARY KEY,
    branch_code VARCHAR(50) UNIQUE,
    branch_name VARCHAR(255),
    address     TEXT,
    phone       VARCHAR(20),
    status      VARCHAR(20),
    created_at  TIMESTAMP DEFAULT NOW(),
    updated_at  TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS employees (
    employee_id   SERIAL PRIMARY KEY,
    user_id       INT,
    branch_id     INT,
    employee_code VARCHAR(50) UNIQUE,
    position      VARCHAR(100),
    status        VARCHAR(20),
    created_at    TIMESTAMP DEFAULT NOW(),
    updated_at    TIMESTAMP DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS bank_accounts (
    bank_account_id SERIAL PRIMARY KEY,
    bank_name       VARCHAR(255),
    account_name    VARCHAR(255),
    account_number  VARCHAR(100) UNIQUE,
    currency        VARCHAR(10),
    status          VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS loan_requests (
    loan_request_id             SERIAL PRIMARY KEY,
    loan_request_number         VARCHAR(50) UNIQUE NOT NULL,
    customer_id                 INT NOT NULL REFERENCES customers(customer_id),
    loan_product_id             INT NOT NULL REFERENCES loan_products(loan_product_id),
    employee_id                 INT,
    branch_id                   INT,
    principal_amount            DECIMAL(15,2),
    currency                    VARCHAR(10),
    duration                    INT,
    duration_unit               VARCHAR(20),
    repayment_frequency         VARCHAR(20),
    monthly_payment_amount      DECIMAL(15,2),
    service_fee_amount          DECIMAL(15,2),
    purpose                     TEXT,
    requested_disbursement_date DATE,
    notes                       TEXT,
    status                      VARCHAR(30),
    created_by                  INT,
    reviewed_by                 INT,
    reviewed_at                 TIMESTAMP,
    review_comment              TEXT,
    created_at                  TIMESTAMP DEFAULT NOW(),
    updated_at                  TIMESTAMP DEFAULT NOW()
);

-- Loans created from a loan request have no application.
ALTER TABLE loans ALTER COLUMN application_id DROP NOT NULL;
ALTER TABLE loans ADD COLUMN IF NOT EXISTS loan_request_id     INT UNIQUE REFERENCES loan_requests(loan_request_id);
ALTER TABLE loans ADD COLUMN IF NOT EXISTS employee_id         INT;
ALTER TABLE loans ADD COLUMN IF NOT EXISTS branch_id           INT;
ALTER TABLE loans ADD COLUMN IF NOT EXISTS currency            VARCHAR(10);
ALTER TABLE loans ADD COLUMN IF NOT EXISTS service_fee_amount  DECIMAL(15,2);
ALTER TABLE loans ADD COLUMN IF NOT EXISTS repayment_frequency VARCHAR(20);

CREATE TABLE IF NOT EXISTS disbursement_transactions (
    disbursement_id   SERIAL PRIMARY KEY,
    loan_id           INT,
    loan_request_id   INT,
    amount            DECIMAL(15,2),
    currency          VARCHAR(10),
    payment_method    VARCHAR(50),
    bank_account_id   INT,
    bank_reference    VARCHAR(100) UNIQUE,
    disbursement_date DATE,
    disbursed_by      INT,
    status            VARCHAR(20),
    notes             TEXT,
    created_at        TIMESTAMP DEFAULT NOW(),
    updated_at        TIMESTAMP DEFAULT NOW()
);

ALTER TABLE payment_transactions ADD COLUMN IF NOT EXISTS bank_reference           VARCHAR(100) UNIQUE;
ALTER TABLE payment_transactions ADD COLUMN IF NOT EXISTS bank_account_id          INT;
ALTER TABLE payment_transactions ADD COLUMN IF NOT EXISTS bank_verification_status VARCHAR(20);
ALTER TABLE payment_transactions ADD COLUMN IF NOT EXISTS idempotency_key          VARCHAR(100) UNIQUE;
ALTER TABLE payment_transactions ADD COLUMN IF NOT EXISTS receipt_number           VARCHAR(50);
ALTER TABLE payment_transactions ADD COLUMN IF NOT EXISTS currency                 VARCHAR(10);
ALTER TABLE payment_transactions ADD COLUMN IF NOT EXISTS notes                    TEXT;
ALTER TABLE payment_transactions ADD COLUMN IF NOT EXISTS is_voided                BOOLEAN DEFAULT FALSE;

CREATE TABLE IF NOT EXISTS collection_ledger (
    ledger_id          SERIAL PRIMARY KEY,
    employee_id        INT,
    branch_id          INT,
    business_date      DATE,
    payment_id         INT,
    transaction_id     INT,
    payment_method     VARCHAR(50),
    principal_amount   DECIMAL(15,2),
    interest_amount    DECIMAL(15,2),
    service_fee_amount DECIMAL(15,2),
    penalty_amount     DECIMAL(15,2),
    total_amount       DECIMAL(15,2),
    currency           VARCHAR(10),
    is_voided          BOOLEAN DEFAULT FALSE,
    created_at         TIMESTAMP DEFAULT NOW(),
    updated_at         TIMESTAMP DEFAULT NOW()
);

-- One row per officer per business day once the day is closed.
-- A closed day blocks further repayments for that officer and date.
CREATE TABLE IF NOT EXISTS daily_closings (
    closing_id                SERIAL PRIMARY KEY,
    employee_id               INT NOT NULL,
    branch_id                 INT,
    business_date             DATE NOT NULL,
    currency                  VARCHAR(10),
    transaction_count         INT,
    cash_expected             DECIMAL(15,2),
    cash_counted              DECIMAL(15,2),
    cash_variance             DECIMAL(15,2),
    bank_transfer_total       DECIMAL(15,2),
    bank_pending_verification DECIMAL(15,2),
    grand_total               DECIMAL(15,2),
    status                    VARCHAR(20),
    notes                     TEXT,
    closed_by                 INT,
    closed_at                 TIMESTAMP,
    created_at                TIMESTAMP DEFAULT NOW(),
    updated_at                TIMESTAMP DEFAULT NOW(),
    UNIQUE (employee_id, business_date)
);

CREATE INDEX IF NOT EXISTS idx_employees_user               ON employees(user_id);
CREATE INDEX IF NOT EXISTS idx_employees_branch             ON employees(branch_id);
CREATE INDEX IF NOT EXISTS idx_loan_requests_customer       ON loan_requests(customer_id);
CREATE INDEX IF NOT EXISTS idx_loan_requests_status         ON loan_requests(status);
CREATE INDEX IF NOT EXISTS idx_loan_requests_product        ON loan_requests(loan_product_id);
CREATE INDEX IF NOT EXISTS idx_disbursements_loan           ON disbursement_transactions(loan_id);
CREATE INDEX IF NOT EXISTS idx_disbursements_loan_request   ON disbursement_transactions(loan_request_id);
CREATE INDEX IF NOT EXISTS idx_ledger_employee              ON collection_ledger(employee_id);
CREATE INDEX IF NOT EXISTS idx_ledger_business_date         ON collection_ledger(business_date);
CREATE INDEX IF NOT EXISTS idx_ledger_transaction           ON collection_ledger(transaction_id);



-- Roles
INSERT INTO roles (role_name, description) VALUES
    ('ADMIN',    'System administrator with full access'),
    ('OFFICER',  'Loan officer who processes and manages loan applications'),
    ('CUSTOMER', 'End customer who applies for loans')
ON CONFLICT (role_name) DO NOTHING;

-- Default admin user
-- password hash below is BCrypt("admin123")
INSERT INTO users (
    role_id, username, email, phone_number, password,
    first_name, last_name, status, email_verified, phone_verified
) VALUES (
    (SELECT role_id FROM roles WHERE role_name = 'ADMIN'),
    'admin',
    'admin@lms.com',
    NULL,
    '$2a$10$GqNCKpgFcfqu6xRPnKBUnOKYH7XeH06cGfdLs3CpkzsQV9Wftlq6W',
    'System',
    'Administrator',
    'Active',
    TRUE,
    FALSE
)
ON CONFLICT (username) DO NOTHING;

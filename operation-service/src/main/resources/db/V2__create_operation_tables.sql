-- CUSTOMERS
CREATE TABLE IF NOT EXISTS customers (
                                         customer_id     SERIAL PRIMARY KEY,
                                         user_id         INT UNIQUE REFERENCES users(user_id),
    first_name      VARCHAR(100) NOT NULL,
    last_name       VARCHAR(100) NOT NULL,
    date_of_birth   DATE,
    gender          VARCHAR(10),
    phone_number    VARCHAR(20) UNIQUE,
    email           VARCHAR(150) UNIQUE,
    national_id     VARCHAR(50) UNIQUE,
    address         TEXT,
    profile_image   VARCHAR(255),
    status          VARCHAR(20) DEFAULT 'Active',
    created_at      TIMESTAMP DEFAULT NOW(),
    updated_at      TIMESTAMP DEFAULT NOW()
    );

CREATE INDEX IF NOT EXISTS idx_customers_email       ON customers(email);
CREATE INDEX IF NOT EXISTS idx_customers_phone       ON customers(phone_number);
CREATE INDEX IF NOT EXISTS idx_customers_national_id ON customers(national_id);

-- EMPLOYMENT HISTORY

CREATE TABLE IF NOT EXISTS employment_history (
                                                  employment_id   SERIAL PRIMARY KEY,
                                                  customer_id     INT NOT NULL REFERENCES customers(customer_id),
    employer_name   VARCHAR(255),
    occupation      VARCHAR(100),
    monthly_income  DECIMAL(15,2),
    start_date      DATE,
    end_date        DATE,
    is_current      BOOLEAN DEFAULT FALSE,
    created_at      TIMESTAMP DEFAULT NOW(),
    updated_at      TIMESTAMP DEFAULT NOW()
    );


-- LOAN PRODUCTS

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


-- LOAN APPLICATIONS

CREATE TABLE IF NOT EXISTS loan_applications (
                                                 application_id      SERIAL PRIMARY KEY,
                                                 application_number  VARCHAR(50) UNIQUE NOT NULL,
    customer_id         INT NOT NULL REFERENCES customers(customer_id),
    loan_product_id     INT NOT NULL REFERENCES loan_products(loan_product_id),
    requested_amount    DECIMAL(15,2),
    requested_term_months INT,
    loan_purpose        TEXT,
    application_status  VARCHAR(30) DEFAULT 'Draft',
    submitted_at        TIMESTAMP,
    created_at          TIMESTAMP DEFAULT NOW(),
    updated_at          TIMESTAMP DEFAULT NOW()
    );

CREATE INDEX IF NOT EXISTS idx_applications_customer ON loan_applications(customer_id);
CREATE INDEX IF NOT EXISTS idx_applications_status   ON loan_applications(application_status);
CREATE INDEX IF NOT EXISTS idx_applications_product  ON loan_applications(loan_product_id);


-- DOCUMENTS

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


-- RISK ASSESSMENTS

CREATE TABLE IF NOT EXISTS risk_assessments (
                                                assessment_id       SERIAL PRIMARY KEY,
                                                application_id      INT NOT NULL REFERENCES loan_applications(application_id),
    risk_score          DECIMAL(5,2),
    risk_level          VARCHAR(20),
    assessment_result   VARCHAR(30),
    assessment_comment  TEXT,
    assessed_by         INT REFERENCES users(user_id),
    assessed_at         TIMESTAMP,
    created_at          TIMESTAMP DEFAULT NOW(),
    updated_at          TIMESTAMP DEFAULT NOW()
    );


-- GUARANTORS

CREATE TABLE IF NOT EXISTS guarantors (
                                          guarantor_id            SERIAL PRIMARY KEY,
                                          application_id          INT NOT NULL REFERENCES loan_applications(application_id),
    first_name              VARCHAR(100),
    last_name               VARCHAR(100),
    date_of_birth           DATE,
    phone_number            VARCHAR(20),
    email                   VARCHAR(150) UNIQUE,
    national_id             VARCHAR(50) UNIQUE,
    address                 TEXT,
    occupation              VARCHAR(100),
    employer                VARCHAR(255),
    monthly_income          DECIMAL(15,2),
    relationship_to_customer VARCHAR(100),
    verification_status     VARCHAR(20) DEFAULT 'Pending',
    verified_by             INT REFERENCES users(user_id),
    verified_at             TIMESTAMP,
    created_at              TIMESTAMP DEFAULT NOW(),
    updated_at              TIMESTAMP DEFAULT NOW()
    );


-- COLLATERALS
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


-- LOAN APPROVALS

CREATE TABLE IF NOT EXISTS loan_approvals (
                                              approval_id     SERIAL PRIMARY KEY,
                                              application_id  INT NOT NULL REFERENCES loan_applications(application_id),
    approved_by     INT NOT NULL REFERENCES users(user_id),
    approval_status VARCHAR(20) DEFAULT 'Pending',
    decision_reason TEXT,
    approved_at     TIMESTAMP,
    created_at      TIMESTAMP DEFAULT NOW(),
    updated_at      TIMESTAMP DEFAULT NOW()
    );


-- LOANS

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

CREATE INDEX IF NOT EXISTS idx_loans_number ON loans(loan_number);
CREATE INDEX IF NOT EXISTS idx_loans_status ON loans(loan_status);


-- LOAN PAYMENTS

CREATE TABLE IF NOT EXISTS loan_payments (
                                             payment_id          SERIAL PRIMARY KEY,
                                             loan_id             INT NOT NULL REFERENCES loans(loan_id),
    payment_no          INT,
    due_date            DATE,
    amount_due          DECIMAL(15,2),
    amount_paid         DECIMAL(15,2) DEFAULT 0,
    remaining_balance   DECIMAL(15,2),
    payment_date        TIMESTAMP,
    payment_status      VARCHAR(20) DEFAULT 'Pending',
    paid_by             VARCHAR(50),
    late_fee            DECIMAL(15,2) DEFAULT 0,
    penalty_amount      DECIMAL(15,2) DEFAULT 0,
    created_at          TIMESTAMP DEFAULT NOW(),
    updated_at          TIMESTAMP DEFAULT NOW()
    );

CREATE INDEX IF NOT EXISTS idx_payments_loan   ON loan_payments(loan_id);
CREATE INDEX IF NOT EXISTS idx_payments_status ON loan_payments(payment_status);
CREATE INDEX IF NOT EXISTS idx_payments_due    ON loan_payments(due_date);


-- PAYMENT TRANSACTIONS

CREATE TABLE IF NOT EXISTS payment_transactions (
                                                    transaction_id      SERIAL PRIMARY KEY,
                                                    payment_id          INT NOT NULL REFERENCES loan_payments(payment_id),
    payment_method      VARCHAR(50),
    amount              DECIMAL(15,2),
    reference_number    VARCHAR(100),
    transaction_status  VARCHAR(20) DEFAULT 'Pending',
    received_by         INT NOT NULL REFERENCES users(user_id),
    transaction_date    TIMESTAMP,
    created_at          TIMESTAMP DEFAULT NOW(),
    updated_at          TIMESTAMP DEFAULT NOW()
    );

CREATE INDEX IF NOT EXISTS idx_transactions_ref ON payment_transactions(reference_number);


-- NOTIFICATIONS

CREATE TABLE IF NOT EXISTS notifications (
                                             notification_id     SERIAL PRIMARY KEY,
                                             customer_id         INT NOT NULL REFERENCES customers(customer_id),
    created_by          INT NOT NULL REFERENCES users(user_id),
    notification_type   VARCHAR(50),
    title               VARCHAR(255),
    message             TEXT,
    notification_status VARCHAR(20) DEFAULT 'Queued',
    channel             VARCHAR(20) DEFAULT 'InApp',
    sent_at             TIMESTAMP,
    is_read             BOOLEAN DEFAULT FALSE,
    read_at             TIMESTAMP,
    created_at          TIMESTAMP DEFAULT NOW(),
    updated_at          TIMESTAMP DEFAULT NOW()
    );

CREATE INDEX IF NOT EXISTS idx_notifications_customer ON notifications(customer_id);
CREATE INDEX IF NOT EXISTS idx_notifications_read     ON notifications(is_read);


-- APPLICATION STATUS HISTORY

CREATE TABLE IF NOT EXISTS application_status_history (
                                                          history_id      SERIAL PRIMARY KEY,
                                                          application_id  INT NOT NULL REFERENCES loan_applications(application_id),
    changed_by      INT NOT NULL REFERENCES users(user_id),
    old_status      VARCHAR(30),
    new_status      VARCHAR(30),
    remark          TEXT,
    created_at      TIMESTAMP DEFAULT NOW(),
    updated_at      TIMESTAMP DEFAULT NOW()
    );
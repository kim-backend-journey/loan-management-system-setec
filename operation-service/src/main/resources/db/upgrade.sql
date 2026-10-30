-- Runs on every operation-service start (spring.sql.init), so a database created from an
-- older init.sql gets the tables and columns the entities need. Every statement must be
-- re-runnable. Keep in step with the matching section of init.sql.

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

-- Every loan officer needs an employee record: disbursement assigns loans to an employee,
-- and due lists, repayments and daily collections are all keyed by employee_id.
INSERT INTO employees (user_id, employee_code, position, status)
SELECT u.user_id, 'EMP-U' || LPAD(u.user_id::text, 4, '0'), 'Loan Officer', 'Active'
FROM users u
JOIN roles r ON r.role_id = u.role_id
WHERE r.role_name = 'OFFICER'
  AND NOT EXISTS (SELECT 1 FROM employees e WHERE e.user_id = u.user_id)
ON CONFLICT (employee_code) DO NOTHING;

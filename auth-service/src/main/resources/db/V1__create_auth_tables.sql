-- ROLES
CREATE TABLE IF NOT EXISTS roles (
                                     role_id     SERIAL PRIMARY KEY,
                                     role_name   VARCHAR(50)  UNIQUE NOT NULL,
    description TEXT
    );

-- USERS
CREATE TABLE IF NOT EXISTS users (
                                     user_id        SERIAL PRIMARY KEY,
                                     role_id        INT REFERENCES roles(role_id),
    username       VARCHAR(100) UNIQUE NOT NULL,
    email          VARCHAR(150) UNIQUE NOT NULL,
    phone_number   VARCHAR(20)  UNIQUE,
    password       VARCHAR(255) NOT NULL,
    first_name     VARCHAR(100),
    last_name      VARCHAR(100),
    status         VARCHAR(20)  DEFAULT 'Active',
    email_verified BOOLEAN      DEFAULT FALSE,
    phone_verified BOOLEAN      DEFAULT FALSE,
    last_login_at  TIMESTAMP,
    created_at     TIMESTAMP    DEFAULT NOW(),
    updated_at     TIMESTAMP    DEFAULT NOW()
    );

CREATE INDEX IF NOT EXISTS idx_users_email    ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_phone    ON users(phone_number);
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);


-- REFRESH TOKENS
CREATE TABLE IF NOT EXISTS refresh_tokens (
                                              refresh_token_id SERIAL PRIMARY KEY,
                                              user_id          INT  NOT NULL REFERENCES users(user_id),
    token            TEXT NOT NULL,
    expires_at       TIMESTAMP,
    revoked          BOOLEAN   DEFAULT FALSE,
    created_at       TIMESTAMP DEFAULT NOW()
    );

-- PASSWORD RESET TOKENS
CREATE TABLE IF NOT EXISTS password_reset_tokens (
                                                     reset_token_id SERIAL PRIMARY KEY,
                                                     user_id        INT  NOT NULL REFERENCES users(user_id),
    token          TEXT NOT NULL,
    expires_at     TIMESTAMP,
    used           BOOLEAN   DEFAULT FALSE,
    created_at     TIMESTAMP DEFAULT NOW()
    );

-- LOGIN HISTORY
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

-- SEED DEFAULT ROLES

INSERT INTO roles (role_name, description) VALUES
                                               ('ADMIN',    'System administrator with full access'),
                                               ('OFFICER',  'Loan officer who reviews applications'),
                                               ('CUSTOMER', 'Customer who applies for loans')
    ON CONFLICT (role_name) DO NOTHING;
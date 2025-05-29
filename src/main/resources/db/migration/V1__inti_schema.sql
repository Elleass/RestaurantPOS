-- Create roles table
CREATE TABLE roles (
                       id_role BIGSERIAL PRIMARY KEY,
                       role_name VARCHAR NOT NULL,
                       description VARCHAR
);

-- Create users table
CREATE TABLE users (
                       id_user BIGSERIAL PRIMARY KEY,
                       username VARCHAR UNIQUE NOT NULL,
                       password VARCHAR NOT NULL,
                       is_locked BOOLEAN DEFAULT FALSE,
                       id_role BIGINT NOT NULL,
                       CONSTRAINT fk_user_role FOREIGN KEY (id_role) REFERENCES roles(id_role)
);

-- Create customers table
CREATE TABLE customers (
                           id_customer BIGSERIAL PRIMARY KEY,
                           name VARCHAR NOT NULL,
                           phone_number VARCHAR,
                           email VARCHAR UNIQUE
);

-- Create categories table
CREATE TABLE categories (
                            id_category BIGSERIAL PRIMARY KEY,
                            category_name VARCHAR UNIQUE NOT NULL
);

-- Create menu_items table
CREATE TABLE menu_items (
                            id_item BIGSERIAL PRIMARY KEY,
                            item_name VARCHAR NOT NULL,
                            description VARCHAR,
                            price DECIMAL(10, 2) NOT NULL,
                            id_category BIGINT NOT NULL,
                            is_available BOOLEAN DEFAULT TRUE,
                            CONSTRAINT fk_menu_category FOREIGN KEY (id_category) REFERENCES categories(id_category)
);

-- Create orders table
CREATE TABLE orders (
                        id_order BIGSERIAL PRIMARY KEY,
                        id_customer BIGINT,
                        id_user BIGINT NOT NULL,
                        order_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        status VARCHAR NOT NULL,
                        total_amount DECIMAL(10, 2) DEFAULT 0,
                        CONSTRAINT fk_order_customer FOREIGN KEY (id_customer) REFERENCES customers(id_customer),
                        CONSTRAINT fk_order_user FOREIGN KEY (id_user) REFERENCES users(id_user)
);

-- Create order_items table
CREATE TABLE order_items (
                             id_order_item BIGSERIAL PRIMARY KEY,
                             id_order BIGINT NOT NULL,
                             id_item BIGINT NOT NULL,
                             quantity INTEGER NOT NULL,
                             item_price DECIMAL(10, 2) NOT NULL,
                             CONSTRAINT fk_order_item_order FOREIGN KEY (id_order) REFERENCES orders(id_order),
                             CONSTRAINT fk_order_item_menu FOREIGN KEY (id_item) REFERENCES menu_items(id_item)
);

-- Create payments table
CREATE TABLE payments (
                          id_payment BIGSERIAL PRIMARY KEY,
                          id_order BIGINT NOT NULL,
                          payment_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          payment_method VARCHAR NOT NULL,
                          amount_paid DECIMAL(10, 2) NOT NULL,
                          is_paid BOOLEAN DEFAULT FALSE,
                          CONSTRAINT fk_payment_order FOREIGN KEY (id_order) REFERENCES orders(id_order)
);

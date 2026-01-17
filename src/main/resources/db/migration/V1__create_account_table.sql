CREATE TABLE account (
                         id UUID NOT NULL,
                         balance_amount NUMERIC(19, 4) NOT NULL,
                         balance_currency VARCHAR(3) NOT NULL,
                         version BIGINT,
                         CONSTRAINT pk_account PRIMARY KEY (id)
);

ALTER TABLE account ADD CONSTRAINT check_currency_length CHECK (LENGTH(balance_currency) = 3);
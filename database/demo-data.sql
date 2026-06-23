-- AGREGAR DOS USUARIOS, UNO CON ROL DE ADMINISTRADOR Y EL OTRO CON ROL DE OPERADOR
-- INSERT USERS (ADMIN + OPERATOR)

INSERT INTO core.user (
    user_name,
    user_role,
    user_password,
    user_status
) VALUES
(
    'admin',
    'ADMIN',
    '$2a$10$sBSeSu2bkbVm6F2gWlOdn..Lhv/gcJneBO.fWPKyohO7g5YwFel9C',
    'ACTIVE'
),
(
    'operador',
    'OPERATOR',
    '$2a$10$lYEhJh841knQI6y.Cub7wuSR2zorfPLsHVjyRWU4y76hAPwEE8xBe',
    'ACTIVE'
);


-- FILAS DE EJEMPLO EN LA TABLA DE PRODUCTOS
INSERT INTO core.product (
    product_code,
    product_name,
    product_price,
    product_stock,
    minimum_stock,
    unit_of_measure,
    product_status
)
SELECT
    'P' || gs AS product_code,

    'Producto ' || gs AS product_name,

    ROUND((500 + random() * 19500)::numeric, 2) AS product_price,

    FLOOR(150000 + random() * 150000) AS product_stock,

    FLOOR(150000 + random() * 150000) AS minimum_stock,

    CASE
        WHEN gs <= 100 THEN 'UNITS'
        WHEN gs <= 200 THEN 'KILOGRAMS'
        ELSE 'LITERS'
    END AS unit_of_measure,

    CASE
        WHEN gs > 210 THEN 'INACTIVE'
        ELSE 'ACTIVE'
    END AS product_status

FROM generate_series(1,230) gs;




-- VENTAS DE EJEMPLO
-- Período: primeros 10 días de marzo de 2026
INSERT INTO core.sale (
    sale_date,
    sale_time,
    total_amount,
    user_id
)
SELECT
    DATE '2026-03-01' + ((gs - 1) / 20) AS sale_date,

    make_time(
        (random() * 23)::int,
        (random() * 59)::int,
        (random() * 59)::int
    ) AS sale_time,

    0 AS total_amount,

    CASE
        WHEN random() < 0.5 THEN 1
        ELSE 2
    END AS user_id

FROM generate_series(1, 200) gs;


-- VENTAS DE EJEMPLO
-- Período: del 11 al 20 de marzo de 2026
INSERT INTO core.sale (
    sale_date,
    sale_time,
    total_amount,
    user_id
)
SELECT
    DATE '2026-03-11' + ((gs - 1) / 30) AS sale_date,

    make_time(
        (random() * 23)::int,
        (random() * 59)::int,
        (random() * 59)::int
    ) AS sale_time,

    0 AS total_amount,

    CASE
        WHEN random() < 0.5 THEN 1
        ELSE 2
    END AS user_id

FROM generate_series(1, 300) gs;

-- VENTAS DE EJEMPLO
-- Período: del 21 al 31 de marzo de 2026
INSERT INTO core.sale (
    sale_date,
    sale_time,
    total_amount,
    user_id
)
SELECT
    DATE '2026-03-21' + ((gs - 1) / 40) AS sale_date,

    make_time(
        (random() * 23)::int,
        (random() * 59)::int,
        (random() * 59)::int
    ) AS sale_time,

    0 AS total_amount,

    CASE
        WHEN random() < 0.5 THEN 1
        ELSE 2
    END AS user_id

FROM generate_series(1, 440) gs;


-- VENTAS DE EJEMPLO
-- Período: del 1 al 30 de abril de 2026
INSERT INTO core.sale (
    sale_date,
    sale_time,
    total_amount,
    user_id
)
SELECT
    DATE '2026-04-01' + ((gs - 1) / 60) AS sale_date,

    make_time(
        (random() * 23)::int,
        (random() * 59)::int,
        (random() * 59)::int
    ) AS sale_time,

    0 AS total_amount,

    CASE
        WHEN random() < 0.5 THEN 1
        ELSE 2
    END AS user_id

FROM generate_series(1, 1800) gs;


-- VENTAS DE EJEMPLO
-- Período: del 1 al 31 de mayo de 2026
INSERT INTO core.sale (
    sale_date,
    sale_time,
    total_amount,
    user_id
)
SELECT
    DATE '2026-05-01' + ((gs - 1) / 120) AS sale_date,

    make_time(
        (random() * 23)::int,
        (random() * 59)::int,
        (random() * 59)::int
    ) AS sale_time,

    0 AS total_amount,

    CASE
        WHEN random() < 0.5 THEN 1
        ELSE 2
    END AS user_id

FROM generate_series(1, 3720) gs;


-- SALE DETAILS PARA VENTAS(MARZO 1-10 DEL 2026)
INSERT INTO core.sale_detail (
    sale_price,
    product_quantity,
    sale_id,
    product_code,
    product_name_at_sale,
    unit_of_measure_at_sale
)
SELECT
    ROUND((500 + random() * 19500)::numeric, 2) AS sale_price,

    FLOOR(1 + random() * 5)::int AS product_quantity,

    sale_id,

    product_code,

    product_name AS product_name_at_sale,

    'UNITS' AS unit_of_measure_at_sale

FROM (
    SELECT
        s.sale_id,
        p.product_code,
        p.product_name,

        ROW_NUMBER() OVER (
            PARTITION BY s.sale_id
            ORDER BY random()
        ) AS rn

    FROM core.sale s
    JOIN core.product p
      ON p.product_code BETWEEN 'P1' AND 'P100'
    WHERE s.sale_id BETWEEN 1 AND 200
) base

WHERE rn <= 5
LIMIT 1000;


-- SALE DETAILS PARA VENTAS(MARZO 11-20 DEL 2026)
INSERT INTO core.sale_detail (
    sale_price,
    product_quantity,
    sale_id,
    product_code,
    product_name_at_sale,
    unit_of_measure_at_sale
)
SELECT
    ROUND((500 + random() * 19500)::numeric, 2) AS sale_price,

    FLOOR(1 + random() * 4)::int AS product_quantity,

    sale_id,

    product_code,

    product_name AS product_name_at_sale,

    'KILOGRAMS' AS unit_of_measure_at_sale

FROM (
    SELECT
        s.sale_id,
        p.product_code,
        p.product_name,

        ROW_NUMBER() OVER (
            PARTITION BY s.sale_id
            ORDER BY random()
        ) AS rn

    FROM core.sale s
    JOIN core.product p
      ON p.product_code BETWEEN 'P101' AND 'P200'
    WHERE s.sale_id BETWEEN 201 AND 500
) base

WHERE rn <= 4
LIMIT 1200;


-- SALE DETAILS PARA VENTAS(MARZO 21-31 DEL 2026)
INSERT INTO core.sale_detail (
    sale_price,
    product_quantity,
    sale_id,
    product_code,
    product_name_at_sale,
    unit_of_measure_at_sale
)
SELECT
    ROUND((500 + random() * 19500)::numeric, 2) AS sale_price,

    FLOOR(1 + random() * 4)::int AS product_quantity,

    sale_id,

    product_code,

    product_name AS product_name_at_sale,

    'KILOGRAMS' AS unit_of_measure_at_sale

FROM (
    SELECT
        s.sale_id,
        p.product_code,
        p.product_name,

        ROW_NUMBER() OVER (
            PARTITION BY s.sale_id
            ORDER BY random()
        ) AS rn

    FROM core.sale s
    JOIN core.product p
      ON p.product_code BETWEEN 'P101' AND 'P200'
    WHERE s.sale_id BETWEEN 501 AND 940
) base

WHERE rn <= 4
LIMIT 1100;

-- SALE DETAILS PARA VENTAS(ABRIL 1-30 DEL 2026)
WITH base_sales AS (
    SELECT generate_series(941, 2740) AS sale_id
),

products AS (
    SELECT
        product_code,
        product_name
    FROM core.product
    WHERE product_code BETWEEN 'P101' AND 'P200'
),

mandatory AS (
    SELECT
        bs.sale_id,
        p.product_code,
        p.product_name,

        ROUND((500 + random() * 19500)::numeric, 2) AS sale_price,
        FLOOR(1 + random() * 4)::numeric AS product_quantity,

        'KILOGRAMS' AS unit_of_measure_at_sale

    FROM base_sales bs
    JOIN LATERAL (
        SELECT * FROM products ORDER BY random() LIMIT 1
    ) p ON true
),

extra AS (
    SELECT
        bs.sale_id,
        p.product_code,
        p.product_name,

        ROUND((500 + random() * 19500)::numeric, 2) AS sale_price,
        FLOOR(1 + random() * 4)::numeric AS product_quantity,

        'KILOGRAMS' AS unit_of_measure_at_sale

    FROM base_sales bs
    JOIN LATERAL (
        SELECT * FROM products ORDER BY random() LIMIT 4
    ) p ON true
),

combined AS (
    SELECT * FROM mandatory
    UNION ALL
    SELECT * FROM extra
)

INSERT INTO core.sale_detail (
    sale_price,
    product_quantity,
    sale_id,
    product_code,
    product_name_at_sale,
    unit_of_measure_at_sale
)
SELECT
    sale_price,
    product_quantity,
    sale_id,
    product_code,
    product_name,
    unit_of_measure_at_sale
FROM (
    SELECT *,
           ROW_NUMBER() OVER () AS rn
    FROM combined
) x
WHERE rn <= 4000;


-- SALE DETAILS PARA VENTAS(MAYO 1-31 DEL 2026)
WITH sales AS (
    SELECT generate_series(3000, 6460) AS sale_id
),

products AS (
    SELECT product_code, product_name
    FROM core.product
    WHERE product_code BETWEEN 'P1' AND 'P80'
),

mandatory AS (
    SELECT
        s.sale_id,
        p.product_code,
        p.product_name,

        ROUND((500 + random() * 19500)::numeric, 2) AS sale_price,

        FLOOR(1 + random() * 2)::int AS product_quantity,

        'UNITS' AS unit_of_measure_at_sale

    FROM sales s
    JOIN LATERAL (
        SELECT * FROM products ORDER BY random() LIMIT 1
    ) p ON true
),

extra AS (
    SELECT
        s.sale_id,
        p.product_code,
        p.product_name,

        ROUND((500 + random() * 19500)::numeric, 2) AS sale_price,

        FLOOR(1 + random() * 2)::int AS product_quantity,

        'UNITS' AS unit_of_measure_at_sale

    FROM sales s
    JOIN LATERAL (
        SELECT * FROM products ORDER BY random() LIMIT 4
    ) p ON true
),

combined AS (
    SELECT * FROM mandatory
    UNION ALL
    SELECT * FROM extra
),

numbered AS (
    SELECT *,
           ROW_NUMBER() OVER () AS rn
    FROM combined
)

INSERT INTO core.sale_detail (
    sale_price,
    product_quantity,
    sale_id,
    product_code,
    product_name_at_sale,
    unit_of_measure_at_sale
)

SELECT
    sale_price,
    product_quantity,
    sale_id,
    product_code,
    product_name,
    unit_of_measure_at_sale

FROM numbered
WHERE rn <= 5000;

-- ELIMINAR VENTAS CON TOTAL IGUAL A 0
DELETE FROM core.sale
WHERE total_amount=0;
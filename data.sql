-- Seed data — only runs if the database is empty

DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM person LIMIT 1) THEN

    INSERT INTO person (id, full_name, gender, age, id_number, address, phone)
    VALUES
      (1, 'Jose Lema',          'M', 21, '1234567890', 'Otavalo SN y principal',   '098254785'),
      (2, 'Marianela Montalvo', 'F', 30, '0123456789', 'Amazonas y NNUU',          '097548965'),
      (3, 'Juan Osorio',        'M', 17, '1713982313', '13 junio y Equinoccial',   '098874587');

    INSERT INTO customer (id, username, password, status)
    VALUES
      (1, 'jose.lema',          '12341234', true),
      (2, 'marianela.montalvo', '56785678', true),
      (3, 'juan.osorio',        '12451245', true);

    -- Advance person sequence so new inserts don't collide
    PERFORM setval('person_id_seq', GREATEST((SELECT MAX(id) FROM person), 1), true);

    INSERT INTO account (id, number, type, initial_balance, balance, daily_balance, daily_balance_reset_date, status, customer_id)
    VALUES
      (1, '478758', 'S', 2000.00, 2000.00, 1000.00, '2026-05-05', true, 1),
      (2, '225487', 'C',  100.00,  100.00, 1000.00, '2026-05-05', true, 2),
      (3, '495878', 'S',    0.00,    0.00, 1000.00, '2026-05-05', true, 3),
      (4, '496825', 'S',  540.00,  540.00, 1000.00, '2026-05-05', true, 2),
      (5, '585545', 'C', 1000.00, 1000.00, 1000.00, '2026-05-05', true, 1);

    -- Advance account sequence so new inserts don't collide
    PERFORM setval('account_id_seq', GREATEST((SELECT MAX(id) FROM account), 1), true);

    RAISE NOTICE 'Seed data inserted successfully.';
  ELSE
    RAISE NOTICE 'Database already has data — seed skipped.';
  END IF;
END $$;

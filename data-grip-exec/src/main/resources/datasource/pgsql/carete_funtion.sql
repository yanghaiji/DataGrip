CREATE
	OR REPLACE FUNCTION generate_create_table_statement ( p_table_name VARCHAR ) RETURNS TEXT AS  $BODY$  DECLARE
	v_table_ddl TEXT;
column_record record;
BEGIN
		FOR column_record IN SELECT
		b.nspname AS SCHEMA_NAME,
		b.relname AS TABLE_NAME,
		A.attname AS COLUMN_NAME,
		pg_catalog.format_type ( A.atttypid, A.atttypmod ) AS column_type,
	CASE

			WHEN (
			SELECT SUBSTRING
				( pg_catalog.pg_get_expr ( d.adbin, d.adrelid ) FOR 128 )
			FROM
				pg_catalog.pg_attrdef d
			WHERE
				d.adrelid = A.attrelid
				AND d.adnum = A.attnum
				AND A.atthasdef
				) IS NOT NULL THEN
				'DEFAULT ' || (
				SELECT SUBSTRING
					( pg_catalog.pg_get_expr ( d.adbin, d.adrelid ) FOR 128 )
				FROM
					pg_catalog.pg_attrdef d
				WHERE
					d.adrelid = A.attrelid
					AND d.adnum = A.attnum
					AND A.atthasdef
				) ELSE''
			END AS column_default_value,
		CASE

				WHEN A.attnotnull = TRUE THEN
				'NOT NULL' ELSE'NULL'
			END AS column_not_null,
			A.attnum AS attnum,
			e.max_attnum AS max_attnum
		FROM
			pg_catalog.pg_attribute
			A INNER JOIN (
			SELECT C
				.oid,
				n.nspname,
				C.relname
			FROM
				pg_catalog.pg_class
				C LEFT JOIN pg_catalog.pg_namespace n ON n.oid = C.relnamespace
			WHERE
				C.relname ~ ( '^(' || p_table_name || ')$' )
				AND pg_catalog.pg_table_is_visible ( C.oid )
			ORDER BY
				2,
				3
			) b ON A.attrelid = b.oid
			INNER JOIN (
			SELECT A
				.attrelid,
				MAX ( A.attnum ) AS max_attnum
			FROM
				pg_catalog.pg_attribute A
			WHERE
				A.attnum > 0
				AND NOT A.attisdropped
			GROUP BY
				A.attrelid
			) e ON A.attrelid = e.attrelid
		WHERE
			A.attnum > 0
			AND NOT A.attisdropped
		ORDER BY
			A.attnum
			LOOP
		IF
			column_record.attnum = 1 THEN
				v_table_ddl := 'CREATE TABLE ' || column_record.SCHEMA_NAME || '.' || column_record.TABLE_NAME || ' (';
			ELSE v_table_ddl := v_table_ddl || ',';

		END IF;
		IF
			column_record.attnum <= column_record.max_attnum THEN
				v_table_ddl := v_table_ddl || chr( 10 ) || '    ' || column_record.COLUMN_NAME || ' ' || column_record.column_type || ' ' || column_record.column_default_value || ' ' || column_record.column_not_null;

		END IF;

	END LOOP;
v_table_ddl := v_table_ddl || ');';
RETURN v_table_ddl;

END;$BODY$ LANGUAGE'plpgsql' COST 100.0 SECURITY INVOKER;
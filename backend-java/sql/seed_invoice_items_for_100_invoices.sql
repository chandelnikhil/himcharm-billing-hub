-- Seeds 1-4 items for invoice IDs 2-101.
-- Product IDs are NULL so this script does not depend on product seed data.
START TRANSACTION;

-- Invoice 2: 1 item(s), subtotal 500, discount 0%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (2, NULL, 'Himachali Woolen Shawl', 1, 500, 0, 500);

-- Invoice 3: 2 item(s), subtotal 637, discount 5%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (3, NULL, 'Traditional Himachali Cap', 1, 318, 5, 302.1);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (3, NULL, 'Woolen Winter Jacket', 1, 319, 5, 303.05);

-- Invoice 4: 3 item(s), subtotal 774, discount 10%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (4, NULL, 'Woolen Winter Jacket', 1, 258, 10, 232.2);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (4, NULL, 'Handmade Woolen Socks', 1, 258, 10, 232.2);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (4, NULL, 'Himachali Woolen Shawl', 1, 258, 10, 232.2);

-- Invoice 5: 4 item(s), subtotal 911, discount 15%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (5, NULL, 'Handmade Woolen Socks', 1, 227, 15, 192.95);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (5, NULL, 'Himachali Woolen Shawl', 1, 227, 15, 192.95);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (5, NULL, 'Traditional Himachali Cap', 1, 227, 15, 192.95);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (5, NULL, 'Woolen Winter Jacket', 1, 230, 15, 195.5);

-- Invoice 6: 1 item(s), subtotal 1048, discount 20%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (6, NULL, 'Himachali Woolen Shawl', 1, 1048, 20, 838.4);

-- Invoice 7: 2 item(s), subtotal 1185, discount 0%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (7, NULL, 'Traditional Himachali Cap', 1, 592, 0, 592);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (7, NULL, 'Woolen Winter Jacket', 1, 593, 0, 593);

-- Invoice 8: 3 item(s), subtotal 1322, discount 5%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (8, NULL, 'Woolen Winter Jacket', 1, 440, 5, 418);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (8, NULL, 'Handmade Woolen Socks', 1, 440, 5, 418);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (8, NULL, 'Himachali Woolen Shawl', 1, 442, 5, 419.9);

-- Invoice 9: 4 item(s), subtotal 1459, discount 10%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (9, NULL, 'Handmade Woolen Socks', 1, 364, 10, 327.6);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (9, NULL, 'Himachali Woolen Shawl', 1, 364, 10, 327.6);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (9, NULL, 'Traditional Himachali Cap', 1, 364, 10, 327.6);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (9, NULL, 'Woolen Winter Jacket', 1, 367, 10, 330.3);

-- Invoice 10: 1 item(s), subtotal 1596, discount 15%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (10, NULL, 'Himachali Woolen Shawl', 1, 1596, 15, 1356.6);

-- Invoice 11: 2 item(s), subtotal 1733, discount 20%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (11, NULL, 'Traditional Himachali Cap', 1, 866, 20, 692.8);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (11, NULL, 'Woolen Winter Jacket', 1, 867, 20, 693.6);

-- Invoice 12: 3 item(s), subtotal 1870, discount 0%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (12, NULL, 'Woolen Winter Jacket', 1, 623, 0, 623);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (12, NULL, 'Handmade Woolen Socks', 1, 623, 0, 623);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (12, NULL, 'Himachali Woolen Shawl', 1, 624, 0, 624);

-- Invoice 13: 4 item(s), subtotal 2007, discount 5%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (13, NULL, 'Handmade Woolen Socks', 1, 501, 5, 475.95);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (13, NULL, 'Himachali Woolen Shawl', 1, 501, 5, 475.95);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (13, NULL, 'Traditional Himachali Cap', 1, 501, 5, 475.95);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (13, NULL, 'Woolen Winter Jacket', 1, 504, 5, 478.8);

-- Invoice 14: 1 item(s), subtotal 2144, discount 10%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (14, NULL, 'Himachali Woolen Shawl', 1, 2144, 10, 1929.6);

-- Invoice 15: 2 item(s), subtotal 2281, discount 15%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (15, NULL, 'Traditional Himachali Cap', 1, 1140, 15, 969);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (15, NULL, 'Woolen Winter Jacket', 1, 1141, 15, 969.85);

-- Invoice 16: 3 item(s), subtotal 2418, discount 20%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (16, NULL, 'Woolen Winter Jacket', 1, 806, 20, 644.8);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (16, NULL, 'Handmade Woolen Socks', 1, 806, 20, 644.8);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (16, NULL, 'Himachali Woolen Shawl', 1, 806, 20, 644.8);

-- Invoice 17: 4 item(s), subtotal 2555, discount 0%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (17, NULL, 'Handmade Woolen Socks', 1, 638, 0, 638);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (17, NULL, 'Himachali Woolen Shawl', 1, 638, 0, 638);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (17, NULL, 'Traditional Himachali Cap', 1, 638, 0, 638);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (17, NULL, 'Woolen Winter Jacket', 1, 641, 0, 641);

-- Invoice 18: 1 item(s), subtotal 2692, discount 5%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (18, NULL, 'Himachali Woolen Shawl', 1, 2692, 5, 2557.4);

-- Invoice 19: 2 item(s), subtotal 2829, discount 10%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (19, NULL, 'Traditional Himachali Cap', 1, 1414, 10, 1272.6);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (19, NULL, 'Woolen Winter Jacket', 1, 1415, 10, 1273.5);

-- Invoice 20: 3 item(s), subtotal 2966, discount 15%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (20, NULL, 'Woolen Winter Jacket', 1, 988, 15, 839.8);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (20, NULL, 'Handmade Woolen Socks', 1, 988, 15, 839.8);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (20, NULL, 'Himachali Woolen Shawl', 1, 990, 15, 841.5);

-- Invoice 21: 4 item(s), subtotal 3103, discount 20%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (21, NULL, 'Handmade Woolen Socks', 1, 775, 20, 620);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (21, NULL, 'Himachali Woolen Shawl', 1, 775, 20, 620);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (21, NULL, 'Traditional Himachali Cap', 1, 775, 20, 620);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (21, NULL, 'Woolen Winter Jacket', 1, 778, 20, 622.4);

-- Invoice 22: 1 item(s), subtotal 3240, discount 0%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (22, NULL, 'Himachali Woolen Shawl', 1, 3240, 0, 3240);

-- Invoice 23: 2 item(s), subtotal 3377, discount 5%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (23, NULL, 'Traditional Himachali Cap', 1, 1688, 5, 1603.6);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (23, NULL, 'Woolen Winter Jacket', 1, 1689, 5, 1604.55);

-- Invoice 24: 3 item(s), subtotal 3514, discount 10%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (24, NULL, 'Woolen Winter Jacket', 1, 1171, 10, 1053.9);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (24, NULL, 'Handmade Woolen Socks', 1, 1171, 10, 1053.9);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (24, NULL, 'Himachali Woolen Shawl', 1, 1172, 10, 1054.8);

-- Invoice 25: 4 item(s), subtotal 3651, discount 15%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (25, NULL, 'Handmade Woolen Socks', 1, 912, 15, 775.2);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (25, NULL, 'Himachali Woolen Shawl', 1, 912, 15, 775.2);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (25, NULL, 'Traditional Himachali Cap', 1, 912, 15, 775.2);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (25, NULL, 'Woolen Winter Jacket', 1, 915, 15, 777.75);

-- Invoice 26: 1 item(s), subtotal 3788, discount 20%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (26, NULL, 'Himachali Woolen Shawl', 1, 3788, 20, 3030.4);

-- Invoice 27: 2 item(s), subtotal 3925, discount 0%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (27, NULL, 'Traditional Himachali Cap', 1, 1962, 0, 1962);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (27, NULL, 'Woolen Winter Jacket', 1, 1963, 0, 1963);

-- Invoice 28: 3 item(s), subtotal 4062, discount 5%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (28, NULL, 'Woolen Winter Jacket', 1, 1354, 5, 1286.3);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (28, NULL, 'Handmade Woolen Socks', 1, 1354, 5, 1286.3);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (28, NULL, 'Himachali Woolen Shawl', 1, 1354, 5, 1286.3);

-- Invoice 29: 4 item(s), subtotal 4199, discount 10%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (29, NULL, 'Handmade Woolen Socks', 1, 1049, 10, 944.1);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (29, NULL, 'Himachali Woolen Shawl', 1, 1049, 10, 944.1);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (29, NULL, 'Traditional Himachali Cap', 1, 1049, 10, 944.1);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (29, NULL, 'Woolen Winter Jacket', 1, 1052, 10, 946.8);

-- Invoice 30: 1 item(s), subtotal 4336, discount 15%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (30, NULL, 'Himachali Woolen Shawl', 1, 4336, 15, 3685.6);

-- Invoice 31: 2 item(s), subtotal 4473, discount 20%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (31, NULL, 'Traditional Himachali Cap', 1, 2236, 20, 1788.8);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (31, NULL, 'Woolen Winter Jacket', 1, 2237, 20, 1789.6);

-- Invoice 32: 3 item(s), subtotal 4610, discount 0%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (32, NULL, 'Woolen Winter Jacket', 1, 1536, 0, 1536);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (32, NULL, 'Handmade Woolen Socks', 1, 1536, 0, 1536);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (32, NULL, 'Himachali Woolen Shawl', 1, 1538, 0, 1538);

-- Invoice 33: 4 item(s), subtotal 4747, discount 5%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (33, NULL, 'Handmade Woolen Socks', 1, 1186, 5, 1126.7);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (33, NULL, 'Himachali Woolen Shawl', 1, 1186, 5, 1126.7);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (33, NULL, 'Traditional Himachali Cap', 1, 1186, 5, 1126.7);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (33, NULL, 'Woolen Winter Jacket', 1, 1189, 5, 1129.55);

-- Invoice 34: 1 item(s), subtotal 4884, discount 10%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (34, NULL, 'Himachali Woolen Shawl', 1, 4884, 10, 4395.6);

-- Invoice 35: 2 item(s), subtotal 5021, discount 15%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (35, NULL, 'Traditional Himachali Cap', 1, 2510, 15, 2133.5);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (35, NULL, 'Woolen Winter Jacket', 1, 2511, 15, 2134.35);

-- Invoice 36: 3 item(s), subtotal 5158, discount 20%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (36, NULL, 'Woolen Winter Jacket', 1, 1719, 20, 1375.2);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (36, NULL, 'Handmade Woolen Socks', 1, 1719, 20, 1375.2);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (36, NULL, 'Himachali Woolen Shawl', 1, 1720, 20, 1376);

-- Invoice 37: 4 item(s), subtotal 5295, discount 0%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (37, NULL, 'Handmade Woolen Socks', 1, 1323, 0, 1323);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (37, NULL, 'Himachali Woolen Shawl', 1, 1323, 0, 1323);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (37, NULL, 'Traditional Himachali Cap', 1, 1323, 0, 1323);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (37, NULL, 'Woolen Winter Jacket', 1, 1326, 0, 1326);

-- Invoice 38: 1 item(s), subtotal 5432, discount 5%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (38, NULL, 'Himachali Woolen Shawl', 1, 5432, 5, 5160.4);

-- Invoice 39: 2 item(s), subtotal 5569, discount 10%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (39, NULL, 'Traditional Himachali Cap', 1, 2784, 10, 2505.6);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (39, NULL, 'Woolen Winter Jacket', 1, 2785, 10, 2506.5);

-- Invoice 40: 3 item(s), subtotal 5706, discount 15%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (40, NULL, 'Woolen Winter Jacket', 1, 1902, 15, 1616.7);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (40, NULL, 'Handmade Woolen Socks', 1, 1902, 15, 1616.7);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (40, NULL, 'Himachali Woolen Shawl', 1, 1902, 15, 1616.7);

-- Invoice 41: 4 item(s), subtotal 5843, discount 20%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (41, NULL, 'Handmade Woolen Socks', 1, 1460, 20, 1168);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (41, NULL, 'Himachali Woolen Shawl', 1, 1460, 20, 1168);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (41, NULL, 'Traditional Himachali Cap', 1, 1460, 20, 1168);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (41, NULL, 'Woolen Winter Jacket', 1, 1463, 20, 1170.4);

-- Invoice 42: 1 item(s), subtotal 5980, discount 0%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (42, NULL, 'Himachali Woolen Shawl', 1, 5980, 0, 5980);

-- Invoice 43: 2 item(s), subtotal 6117, discount 5%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (43, NULL, 'Traditional Himachali Cap', 1, 3058, 5, 2905.1);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (43, NULL, 'Woolen Winter Jacket', 1, 3059, 5, 2906.05);

-- Invoice 44: 3 item(s), subtotal 6254, discount 10%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (44, NULL, 'Woolen Winter Jacket', 1, 2084, 10, 1875.6);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (44, NULL, 'Handmade Woolen Socks', 1, 2084, 10, 1875.6);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (44, NULL, 'Himachali Woolen Shawl', 1, 2086, 10, 1877.4);

-- Invoice 45: 4 item(s), subtotal 6391, discount 15%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (45, NULL, 'Handmade Woolen Socks', 1, 1597, 15, 1357.45);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (45, NULL, 'Himachali Woolen Shawl', 1, 1597, 15, 1357.45);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (45, NULL, 'Traditional Himachali Cap', 1, 1597, 15, 1357.45);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (45, NULL, 'Woolen Winter Jacket', 1, 1600, 15, 1360);

-- Invoice 46: 1 item(s), subtotal 6528, discount 20%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (46, NULL, 'Himachali Woolen Shawl', 1, 6528, 20, 5222.4);

-- Invoice 47: 2 item(s), subtotal 6665, discount 0%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (47, NULL, 'Traditional Himachali Cap', 1, 3332, 0, 3332);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (47, NULL, 'Woolen Winter Jacket', 1, 3333, 0, 3333);

-- Invoice 48: 3 item(s), subtotal 6802, discount 5%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (48, NULL, 'Woolen Winter Jacket', 1, 2267, 5, 2153.65);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (48, NULL, 'Handmade Woolen Socks', 1, 2267, 5, 2153.65);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (48, NULL, 'Himachali Woolen Shawl', 1, 2268, 5, 2154.6);

-- Invoice 49: 4 item(s), subtotal 6939, discount 10%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (49, NULL, 'Handmade Woolen Socks', 1, 1734, 10, 1560.6);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (49, NULL, 'Himachali Woolen Shawl', 1, 1734, 10, 1560.6);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (49, NULL, 'Traditional Himachali Cap', 1, 1734, 10, 1560.6);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (49, NULL, 'Woolen Winter Jacket', 1, 1737, 10, 1563.3);

-- Invoice 50: 1 item(s), subtotal 7076, discount 15%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (50, NULL, 'Himachali Woolen Shawl', 1, 7076, 15, 6014.6);

-- Invoice 51: 2 item(s), subtotal 7213, discount 20%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (51, NULL, 'Traditional Himachali Cap', 1, 3606, 20, 2884.8);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (51, NULL, 'Woolen Winter Jacket', 1, 3607, 20, 2885.6);

-- Invoice 52: 3 item(s), subtotal 7350, discount 0%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (52, NULL, 'Woolen Winter Jacket', 1, 2450, 0, 2450);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (52, NULL, 'Handmade Woolen Socks', 1, 2450, 0, 2450);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (52, NULL, 'Himachali Woolen Shawl', 1, 2450, 0, 2450);

-- Invoice 53: 4 item(s), subtotal 7487, discount 5%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (53, NULL, 'Handmade Woolen Socks', 1, 1871, 5, 1777.45);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (53, NULL, 'Himachali Woolen Shawl', 1, 1871, 5, 1777.45);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (53, NULL, 'Traditional Himachali Cap', 1, 1871, 5, 1777.45);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (53, NULL, 'Woolen Winter Jacket', 1, 1874, 5, 1780.3);

-- Invoice 54: 1 item(s), subtotal 7624, discount 10%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (54, NULL, 'Himachali Woolen Shawl', 1, 7624, 10, 6861.6);

-- Invoice 55: 2 item(s), subtotal 7761, discount 15%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (55, NULL, 'Traditional Himachali Cap', 1, 3880, 15, 3298);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (55, NULL, 'Woolen Winter Jacket', 1, 3881, 15, 3298.85);

-- Invoice 56: 3 item(s), subtotal 7898, discount 20%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (56, NULL, 'Woolen Winter Jacket', 1, 2632, 20, 2105.6);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (56, NULL, 'Handmade Woolen Socks', 1, 2632, 20, 2105.6);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (56, NULL, 'Himachali Woolen Shawl', 1, 2634, 20, 2107.2);

-- Invoice 57: 4 item(s), subtotal 8035, discount 0%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (57, NULL, 'Handmade Woolen Socks', 1, 2008, 0, 2008);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (57, NULL, 'Himachali Woolen Shawl', 1, 2008, 0, 2008);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (57, NULL, 'Traditional Himachali Cap', 1, 2008, 0, 2008);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (57, NULL, 'Woolen Winter Jacket', 1, 2011, 0, 2011);

-- Invoice 58: 1 item(s), subtotal 8172, discount 5%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (58, NULL, 'Himachali Woolen Shawl', 1, 8172, 5, 7763.4);

-- Invoice 59: 2 item(s), subtotal 8309, discount 10%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (59, NULL, 'Traditional Himachali Cap', 1, 4154, 10, 3738.6);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (59, NULL, 'Woolen Winter Jacket', 1, 4155, 10, 3739.5);

-- Invoice 60: 3 item(s), subtotal 8446, discount 15%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (60, NULL, 'Woolen Winter Jacket', 1, 2815, 15, 2392.75);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (60, NULL, 'Handmade Woolen Socks', 1, 2815, 15, 2392.75);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (60, NULL, 'Himachali Woolen Shawl', 1, 2816, 15, 2393.6);

-- Invoice 61: 4 item(s), subtotal 8583, discount 20%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (61, NULL, 'Handmade Woolen Socks', 1, 2145, 20, 1716);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (61, NULL, 'Himachali Woolen Shawl', 1, 2145, 20, 1716);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (61, NULL, 'Traditional Himachali Cap', 1, 2145, 20, 1716);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (61, NULL, 'Woolen Winter Jacket', 1, 2148, 20, 1718.4);

-- Invoice 62: 1 item(s), subtotal 8720, discount 0%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (62, NULL, 'Himachali Woolen Shawl', 1, 8720, 0, 8720);

-- Invoice 63: 2 item(s), subtotal 8857, discount 5%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (63, NULL, 'Traditional Himachali Cap', 1, 4428, 5, 4206.6);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (63, NULL, 'Woolen Winter Jacket', 1, 4429, 5, 4207.55);

-- Invoice 64: 3 item(s), subtotal 8994, discount 10%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (64, NULL, 'Woolen Winter Jacket', 1, 2998, 10, 2698.2);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (64, NULL, 'Handmade Woolen Socks', 1, 2998, 10, 2698.2);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (64, NULL, 'Himachali Woolen Shawl', 1, 2998, 10, 2698.2);

-- Invoice 65: 4 item(s), subtotal 9131, discount 15%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (65, NULL, 'Handmade Woolen Socks', 1, 2282, 15, 1939.7);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (65, NULL, 'Himachali Woolen Shawl', 1, 2282, 15, 1939.7);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (65, NULL, 'Traditional Himachali Cap', 1, 2282, 15, 1939.7);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (65, NULL, 'Woolen Winter Jacket', 1, 2285, 15, 1942.25);

-- Invoice 66: 1 item(s), subtotal 9268, discount 20%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (66, NULL, 'Himachali Woolen Shawl', 1, 9268, 20, 7414.4);

-- Invoice 67: 2 item(s), subtotal 9405, discount 0%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (67, NULL, 'Traditional Himachali Cap', 1, 4702, 0, 4702);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (67, NULL, 'Woolen Winter Jacket', 1, 4703, 0, 4703);

-- Invoice 68: 3 item(s), subtotal 9542, discount 5%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (68, NULL, 'Woolen Winter Jacket', 1, 3180, 5, 3021);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (68, NULL, 'Handmade Woolen Socks', 1, 3180, 5, 3021);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (68, NULL, 'Himachali Woolen Shawl', 1, 3182, 5, 3022.9);

-- Invoice 69: 4 item(s), subtotal 9679, discount 10%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (69, NULL, 'Handmade Woolen Socks', 1, 2419, 10, 2177.1);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (69, NULL, 'Himachali Woolen Shawl', 1, 2419, 10, 2177.1);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (69, NULL, 'Traditional Himachali Cap', 1, 2419, 10, 2177.1);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (69, NULL, 'Woolen Winter Jacket', 1, 2422, 10, 2179.8);

-- Invoice 70: 1 item(s), subtotal 9816, discount 15%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (70, NULL, 'Himachali Woolen Shawl', 1, 9816, 15, 8343.6);

-- Invoice 71: 2 item(s), subtotal 9953, discount 20%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (71, NULL, 'Traditional Himachali Cap', 1, 4976, 20, 3980.8);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (71, NULL, 'Woolen Winter Jacket', 1, 4977, 20, 3981.6);

-- Invoice 72: 3 item(s), subtotal 590, discount 0%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (72, NULL, 'Woolen Winter Jacket', 1, 196, 0, 196);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (72, NULL, 'Handmade Woolen Socks', 1, 196, 0, 196);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (72, NULL, 'Himachali Woolen Shawl', 1, 198, 0, 198);

-- Invoice 73: 4 item(s), subtotal 727, discount 5%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (73, NULL, 'Handmade Woolen Socks', 1, 181, 5, 171.95);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (73, NULL, 'Himachali Woolen Shawl', 1, 181, 5, 171.95);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (73, NULL, 'Traditional Himachali Cap', 1, 181, 5, 171.95);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (73, NULL, 'Woolen Winter Jacket', 1, 184, 5, 174.8);

-- Invoice 74: 1 item(s), subtotal 864, discount 10%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (74, NULL, 'Himachali Woolen Shawl', 1, 864, 10, 777.6);

-- Invoice 75: 2 item(s), subtotal 1001, discount 15%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (75, NULL, 'Traditional Himachali Cap', 1, 500, 15, 425);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (75, NULL, 'Woolen Winter Jacket', 1, 501, 15, 425.85);

-- Invoice 76: 3 item(s), subtotal 1138, discount 20%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (76, NULL, 'Woolen Winter Jacket', 1, 379, 20, 303.2);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (76, NULL, 'Handmade Woolen Socks', 1, 379, 20, 303.2);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (76, NULL, 'Himachali Woolen Shawl', 1, 380, 20, 304);

-- Invoice 77: 4 item(s), subtotal 1275, discount 0%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (77, NULL, 'Handmade Woolen Socks', 1, 318, 0, 318);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (77, NULL, 'Himachali Woolen Shawl', 1, 318, 0, 318);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (77, NULL, 'Traditional Himachali Cap', 1, 318, 0, 318);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (77, NULL, 'Woolen Winter Jacket', 1, 321, 0, 321);

-- Invoice 78: 1 item(s), subtotal 1412, discount 5%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (78, NULL, 'Himachali Woolen Shawl', 1, 1412, 5, 1341.4);

-- Invoice 79: 2 item(s), subtotal 1549, discount 10%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (79, NULL, 'Traditional Himachali Cap', 1, 774, 10, 696.6);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (79, NULL, 'Woolen Winter Jacket', 1, 775, 10, 697.5);

-- Invoice 80: 3 item(s), subtotal 1686, discount 15%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (80, NULL, 'Woolen Winter Jacket', 1, 562, 15, 477.7);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (80, NULL, 'Handmade Woolen Socks', 1, 562, 15, 477.7);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (80, NULL, 'Himachali Woolen Shawl', 1, 562, 15, 477.7);

-- Invoice 81: 4 item(s), subtotal 1823, discount 20%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (81, NULL, 'Handmade Woolen Socks', 1, 455, 20, 364);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (81, NULL, 'Himachali Woolen Shawl', 1, 455, 20, 364);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (81, NULL, 'Traditional Himachali Cap', 1, 455, 20, 364);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (81, NULL, 'Woolen Winter Jacket', 1, 458, 20, 366.4);

-- Invoice 82: 1 item(s), subtotal 1960, discount 0%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (82, NULL, 'Himachali Woolen Shawl', 1, 1960, 0, 1960);

-- Invoice 83: 2 item(s), subtotal 2097, discount 5%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (83, NULL, 'Traditional Himachali Cap', 1, 1048, 5, 995.6);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (83, NULL, 'Woolen Winter Jacket', 1, 1049, 5, 996.55);

-- Invoice 84: 3 item(s), subtotal 2234, discount 10%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (84, NULL, 'Woolen Winter Jacket', 1, 744, 10, 669.6);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (84, NULL, 'Handmade Woolen Socks', 1, 744, 10, 669.6);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (84, NULL, 'Himachali Woolen Shawl', 1, 746, 10, 671.4);

-- Invoice 85: 4 item(s), subtotal 2371, discount 15%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (85, NULL, 'Handmade Woolen Socks', 1, 592, 15, 503.2);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (85, NULL, 'Himachali Woolen Shawl', 1, 592, 15, 503.2);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (85, NULL, 'Traditional Himachali Cap', 1, 592, 15, 503.2);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (85, NULL, 'Woolen Winter Jacket', 1, 595, 15, 505.75);

-- Invoice 86: 1 item(s), subtotal 2508, discount 20%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (86, NULL, 'Himachali Woolen Shawl', 1, 2508, 20, 2006.4);

-- Invoice 87: 2 item(s), subtotal 2645, discount 0%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (87, NULL, 'Traditional Himachali Cap', 1, 1322, 0, 1322);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (87, NULL, 'Woolen Winter Jacket', 1, 1323, 0, 1323);

-- Invoice 88: 3 item(s), subtotal 2782, discount 5%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (88, NULL, 'Woolen Winter Jacket', 1, 927, 5, 880.65);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (88, NULL, 'Handmade Woolen Socks', 1, 927, 5, 880.65);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (88, NULL, 'Himachali Woolen Shawl', 1, 928, 5, 881.6);

-- Invoice 89: 4 item(s), subtotal 2919, discount 10%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (89, NULL, 'Handmade Woolen Socks', 1, 729, 10, 656.1);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (89, NULL, 'Himachali Woolen Shawl', 1, 729, 10, 656.1);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (89, NULL, 'Traditional Himachali Cap', 1, 729, 10, 656.1);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (89, NULL, 'Woolen Winter Jacket', 1, 732, 10, 658.8);

-- Invoice 90: 1 item(s), subtotal 3056, discount 15%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (90, NULL, 'Himachali Woolen Shawl', 1, 3056, 15, 2597.6);

-- Invoice 91: 2 item(s), subtotal 3193, discount 20%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (91, NULL, 'Traditional Himachali Cap', 1, 1596, 20, 1276.8);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (91, NULL, 'Woolen Winter Jacket', 1, 1597, 20, 1277.6);

-- Invoice 92: 3 item(s), subtotal 3330, discount 0%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (92, NULL, 'Woolen Winter Jacket', 1, 1110, 0, 1110);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (92, NULL, 'Handmade Woolen Socks', 1, 1110, 0, 1110);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (92, NULL, 'Himachali Woolen Shawl', 1, 1110, 0, 1110);

-- Invoice 93: 4 item(s), subtotal 3467, discount 5%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (93, NULL, 'Handmade Woolen Socks', 1, 866, 5, 822.7);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (93, NULL, 'Himachali Woolen Shawl', 1, 866, 5, 822.7);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (93, NULL, 'Traditional Himachali Cap', 1, 866, 5, 822.7);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (93, NULL, 'Woolen Winter Jacket', 1, 869, 5, 825.55);

-- Invoice 94: 1 item(s), subtotal 3604, discount 10%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (94, NULL, 'Himachali Woolen Shawl', 1, 3604, 10, 3243.6);

-- Invoice 95: 2 item(s), subtotal 3741, discount 15%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (95, NULL, 'Traditional Himachali Cap', 1, 1870, 15, 1589.5);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (95, NULL, 'Woolen Winter Jacket', 1, 1871, 15, 1590.35);

-- Invoice 96: 3 item(s), subtotal 3878, discount 20%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (96, NULL, 'Woolen Winter Jacket', 1, 1292, 20, 1033.6);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (96, NULL, 'Handmade Woolen Socks', 1, 1292, 20, 1033.6);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (96, NULL, 'Himachali Woolen Shawl', 1, 1294, 20, 1035.2);

-- Invoice 97: 4 item(s), subtotal 4015, discount 0%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (97, NULL, 'Handmade Woolen Socks', 1, 1003, 0, 1003);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (97, NULL, 'Himachali Woolen Shawl', 1, 1003, 0, 1003);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (97, NULL, 'Traditional Himachali Cap', 1, 1003, 0, 1003);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (97, NULL, 'Woolen Winter Jacket', 1, 1006, 0, 1006);

-- Invoice 98: 1 item(s), subtotal 4152, discount 5%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (98, NULL, 'Himachali Woolen Shawl', 1, 4152, 5, 3944.4);

-- Invoice 99: 2 item(s), subtotal 4289, discount 10%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (99, NULL, 'Traditional Himachali Cap', 1, 2144, 10, 1929.6);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (99, NULL, 'Woolen Winter Jacket', 1, 2145, 10, 1930.5);

-- Invoice 100: 3 item(s), subtotal 4426, discount 15%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (100, NULL, 'Woolen Winter Jacket', 1, 1475, 15, 1253.75);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (100, NULL, 'Handmade Woolen Socks', 1, 1475, 15, 1253.75);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (100, NULL, 'Himachali Woolen Shawl', 1, 1476, 15, 1254.6);

-- Invoice 101: 4 item(s), subtotal 4563, discount 20%
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (101, NULL, 'Handmade Woolen Socks', 1, 1140, 20, 912);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (101, NULL, 'Himachali Woolen Shawl', 1, 1140, 20, 912);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (101, NULL, 'Traditional Himachali Cap', 1, 1140, 20, 912);
INSERT INTO invoice_items (invoice_id, product_id, item_name, quantity, unit_price, discount_percentage, line_total) VALUES (101, NULL, 'Woolen Winter Jacket', 1, 1143, 20, 914.4);

COMMIT;

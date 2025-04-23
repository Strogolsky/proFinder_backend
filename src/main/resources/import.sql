-- Locations --
INSERT INTO location (name) VALUES
('Prague'),
('Brno'),
('Ostrava'),
('Pilsen'),
('Liberec'),
('Olomouc'),
('České Budějovice'),
('Hradec Králové'),
('Zlín'),
('Karlovy Vary');

-- Services --
INSERT INTO service_offering (name) VALUES
('Haircut'),
('Legal Consultation'),
('Tax Filing'),
('Skin Care Treatment'),
('Laptop Repair'),
('Phone Screen Replacement'),
('Resume Review'),
('Business Registration Help'),
('Makeup Session'),
('Photo Editing'),
('Interior Design Consultation'),
('Translation of Documents'),
('Tutoring in Math'),
('Guitar Lessons'),
('Logo Design'),
('Website Development'),
('Plumbing Repair'),
('Furniture Assembly'),
('Pet Grooming'),
('Fitness Coaching');


-- Users (Specialists)
INSERT INTO users (id, email, password, first_name, last_name, phone_number, location_id, role, createat) VALUES
(3, 'spec1@example.com', '$2a$10$abcdefghijklmnopqrstuv', 'Eva', 'Kralova', '555555555', 1, 'SPECIALIST', now()),
(4, 'spec2@example.com', '$2a$10$abcdefghijklmnopqrstuv', 'Tomas', 'Dvorak', '444444444', 2, 'SPECIALIST', now()),
(5, 'spec3@example.com', '$2a$10$abcdefghijklmnopqrstuv', 'Lucie', 'Horakova', '111222333', 1, 'SPECIALIST', now()),
(6, 'spec4@example.com', '$2a$10$abcdefghijklmnopqrstuv', 'Petr', 'Novotny', '222333444', 2, 'SPECIALIST', now()),
(7, 'spec5@example.com', '$2a$10$abcdefghijklmnopqrstuv', 'Karel', 'Sedlak', '333444555', 1, 'SPECIALIST', now()),
(8, 'spec6@example.com', '$2a$10$abcdefghijklmnopqrstuv', 'Monika', 'Urbanova', '444555666', 2, 'SPECIALIST', now()),
(9, 'spec7@example.com', '$2a$10$abcdefghijklmnopqrstuv', 'Jana', 'Prochazkova', '555666777', 1, 'SPECIALIST', now()),
(10, 'spec8@example.com', '$2a$10$abcdefghijklmnopqrstuv', 'Martin', 'Cerny', '666777888', 2, 'SPECIALIST', now()),
(11, 'spec9@example.com', '$2a$10$abcdefghijklmnopqrstuv', 'Alena', 'Kozlova', '777888999', 1, 'SPECIALIST', now()),
(12, 'spec10@example.com', '$2a$10$abcdefghijklmnopqrstuv', 'Marek', 'Blaha', '888999000', 2, 'SPECIALIST', now());

-- Specialist data
INSERT INTO specialist (id, average_rating, description) VALUES
(3, 4.7, 'Experienced hair stylist with over 10 years in the business.'),
(4, 4.2, 'Certified tax consultant helping small businesses.'),
(5, 4.5, 'Expert in legal consulting and personal finance.'),
(6, 4.3, 'Tech enthusiast offering laptop and phone repair.'),
(7, 4.6, 'Haircuts and grooming for men and women.'),
(8, 4.1, 'Professional resume and job application advisor.'),
(9, 4.4, 'Skincare specialist with years of experience.'),
(10, 4.0, 'Document translation and business help services.'),
(11, 4.8, 'Photo editing and logo design expert.'),
(12, 4.9, 'Website development and IT consulting.');

-- Specialist services
INSERT INTO specialist_service_offering (specialist_id, serviceofferings_id) VALUES
(3, 1), (3, 4),
(4, 3), (4, 2),
(5, 2), (5, 3),
(6, 5), (6, 6),
(7, 1), (7, 3),
(8, 7), (8, 2),
(9, 4), (9, 1),
(10, 8), (10, 12),
(11, 10), (11, 15),
(12, 16), (12, 8);

-- Seed Personnel
INSERT INTO personnel (id, first_name, last_name, rank, unit, email, active) VALUES
(1, 'James', 'Mitchell', 'SSG', '1st Logistics Battalion', 'j.mitchell@army.mil', true),
(2, 'Sandra', 'Torres', 'CPT', 'HHC 3rd Brigade', 's.torres@army.mil', true),
(3, 'Kevin', 'Wallace', 'SPC', '204th Support Company', 'k.wallace@army.mil', true),
(4, 'Diana', 'Chen', 'LTC', 'G4 Division', 'd.chen@army.mil', true);

-- Seed Assets
INSERT INTO asset (id, serial_number, name, category, status, assigned_to_id) VALUES
(1, 'SN-M4-00142', 'M4 Carbine', 'WEAPON', 'ASSIGNED', 1),
(2, 'SN-HMMWV-0887', 'HMMWV', 'VEHICLE', 'ASSIGNED', 2),
(3, 'SN-PRC152-0034', 'AN/PRC-152 Radio', 'COMMS', 'ASSIGNED', 1),
(4, 'SN-LAPTOP-2201', 'Toughbook CF-33', 'EQUIPMENT', 'ASSIGNED', 4),
(5, 'SN-M4-00199', 'M4 Carbine', 'WEAPON', 'UNASSIGNED', null),
(6, 'SN-GEN-0055', 'Generator 5KW', 'EQUIPMENT', 'MAINTENANCE', null);

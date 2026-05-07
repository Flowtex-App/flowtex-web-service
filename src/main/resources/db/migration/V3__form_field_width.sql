-- Add column for grid column span (1-12) per form field.
ALTER TABLE form_fields ADD COLUMN width INT NOT NULL DEFAULT 12;

-- Initial widths for seeded fields (mix of full, half, third).
UPDATE form_fields SET width = 12 WHERE field_key IN ('full_name', 'description', 'change_description', 'rollback_plan', 'incident_datetime', 'company_name');
UPDATE form_fields SET width = 6  WHERE field_key IN ('corporate_email', 'required_date', 'estimated_amount', 'cost_center', 'urgency', 'incident_type', 'affected_systems', 'evidence', 'start_date', 'end_date', 'leave_type', 'backup_person', 'tax_id', 'change_title', 'change_type', 'deployment_window');
UPDATE form_fields SET width = 4  WHERE field_key IN ('required_systems', 'purchase_concept', 'quotation_file', 'certifications', 'references', 'impacted_systems');

-- V6: persist visual properties of the workflow graph.
--   * source/target handles per transition (which side of each node the edge attaches to)
--   * curated color name per step (visual differentiation in the canvas)

ALTER TABLE workflow_step_transitions ADD COLUMN source_handle VARCHAR(8);
ALTER TABLE workflow_step_transitions ADD COLUMN target_handle VARCHAR(8);

ALTER TABLE workflow_steps ADD COLUMN color VARCHAR(20);

-- Seed: rough handles + colors so the existing flow reads as a clean diagram.
UPDATE workflow_step_transitions SET source_handle = 'right',  target_handle = 'left' WHERE from_step_id = 1 AND to_step_id = 2;
UPDATE workflow_step_transitions SET source_handle = 'bottom', target_handle = 'top'  WHERE from_step_id = 1 AND to_step_id IS NULL;
UPDATE workflow_step_transitions SET source_handle = 'bottom', target_handle = 'top'  WHERE from_step_id = 2 AND to_step_id IS NULL;

UPDATE workflow_steps SET color = 'sky'    WHERE id = 1;
UPDATE workflow_steps SET color = 'violet' WHERE id = 2;

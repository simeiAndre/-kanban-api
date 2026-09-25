CREATE TABLE projects (
    id UUID PRIMARY KEY,
    name VARCHAR(180) NOT NULL,
    status VARCHAR(30) NOT NULL,
    planned_start_date DATE,
    planned_end_date DATE,
    actual_start_date DATE,
    actual_end_date DATE,
    delay_days BIGINT NOT NULL DEFAULT 0,
    remaining_time_percentage DOUBLE PRECISION NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE project_responsibles (
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    responsible_id UUID NOT NULL REFERENCES responsibles(id),
    PRIMARY KEY (project_id, responsible_id)
);

CREATE INDEX idx_projects_status ON projects (status);
CREATE INDEX idx_projects_planned_end_date ON projects (planned_end_date);
CREATE INDEX idx_project_responsibles_responsible ON project_responsibles (responsible_id);
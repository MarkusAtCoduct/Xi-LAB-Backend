package com.codeleap.xilab.api.models.Audit;

import com.codeleap.xilab.api.models.entities.Audit;

import java.time.LocalDateTime;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

public class AuditListener {

	@PrePersist
	public void setCreatedOn(Auditable auditable) {
		Audit audit = auditable.getAudit();

		if (audit == null) {
			audit = new Audit();
			auditable.setAudit(audit);
		}
		audit.setCreatedOn(LocalDateTime.now());
	}

	@PreUpdate
	public void setUpdatedOn(Auditable auditable) {
		Audit audit = auditable.getAudit();
		audit.setUpdatedOn(LocalDateTime.now());
	}

}

package com.codeleap.xilab.api.models.Audit;

import com.codeleap.xilab.api.models.entities.Audit;

public interface Auditable {

	Audit getAudit();

	void setAudit(Audit audit);

}

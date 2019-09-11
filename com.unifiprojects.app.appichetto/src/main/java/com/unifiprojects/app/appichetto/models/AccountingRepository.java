package com.unifiprojects.app.appichetto.models;

import java.util.List;

public interface AccountingRepository {
	public void saveAccounting(Accounting accounting);
	public List<Accounting> getAccountinsOf(User user);
	public Accounting getById(long id);
}

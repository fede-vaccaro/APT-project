package com.unifiprojects.app.appichetto.repositories;

import java.util.List;

import com.unifiprojects.app.appichetto.models.Accounting;
import com.unifiprojects.app.appichetto.models.User;

public interface AccountingRepository {
	public void saveAccounting(Accounting accounting);

	public List<Accounting> getAccountingsOf(User user);

	public Accounting getById(long id);
}

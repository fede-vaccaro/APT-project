package com.unifiprojects.app.appichetto.bdd.steps;

import java.util.Locale;
import java.util.Map;

import com.unifiprojects.app.appichetto.models.User;

import io.cucumber.core.api.TypeRegistry;
import io.cucumber.core.api.TypeRegistryConfigurer;
import io.cucumber.datatable.DataTableType;
import io.cucumber.datatable.TableEntryTransformer;

public class ParameterTypes implements TypeRegistryConfigurer {

	@Override
	public Locale locale() {
		return Locale.ENGLISH;
	}

	@Override
	public void configureTypeRegistry(TypeRegistry typeRegistry) {

		typeRegistry.defineDataTableType(new DataTableType(User.class, new TableEntryTransformer<User>() {
			@Override
			public User transform(Map<String, String> entry) throws Throwable {
				return new User(entry.get("name"), entry.get("password"));
			}
		}));
	}

}

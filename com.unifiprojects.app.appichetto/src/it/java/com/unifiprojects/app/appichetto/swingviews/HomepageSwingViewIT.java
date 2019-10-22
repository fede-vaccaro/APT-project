package com.unifiprojects.app.appichetto.swingviews;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.unifiprojects.app.appichetto.modules.EntityManagerModule;
import com.unifiprojects.app.appichetto.modules.PayReceiptsModule;
import com.unifiprojects.app.appichetto.modules.RepositoriesModule;

@RunWith(GUITestRunner.class)

public class HomepageSwingViewIT extends AssertJSwingJUnitTestCase {

	private static Injector injector;

	private HomepageSwingView homepageSwingView;
	
	private FrameFixture window;

	@Mock
	private ObservableFrameSwing loginView;
	@Mock
	private ObservableFrameSwing receiptView;
	@Mock
	private ObservableFrameSwing historyView;

	
	@BeforeClass
	public static void setUpInjectors() {
			
		Module repositoriesModule = new RepositoriesModule();
		
		Module entityManagerModule = new EntityManagerModule();

		Module payReceiptModule = new PayReceiptsModule();

		Injector persistenceInjector = Guice.createInjector(entityManagerModule);
		
		// baseTest = persistenceInjector.getInstance(MVCBaseTest.class);
		// entityManager = persistenceInjector.getInstance(EntityManager.class);
		
		injector = persistenceInjector.createChildInjector(repositoriesModule, payReceiptModule);

	}
	
	@Override
	protected void onSetUp() {
		MockitoAnnotations.initMocks(this);

		GuiActionRunner.execute(() -> {
			homepageSwingView = injector.getInstance(HomepageSwingView.class);
			homepageSwingView.setLoginView(loginView);
			homepageSwingView.setReceiptSwingView(receiptView);
			homepageSwingView.setHistoryViewSwing(historyView);

		});
		window = new FrameFixture(robot(), homepageSwingView.getFrame());
		window.show();

	}
	
	@GUITest
	@Test
	public void testInjection() {
		assertThat(homepageSwingView.payReceiptsView).isNotNull();
		PayReceiptsViewSwing payReceiptsViewSwing = (PayReceiptsViewSwing)homepageSwingView.payReceiptsView;
		assertThat( payReceiptsViewSwing .getController()).isNotNull();
	}


}

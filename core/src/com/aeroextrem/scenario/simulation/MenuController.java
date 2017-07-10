package com.aeroextrem.scenario.simulation;

import com.aeroextrem.database.RecordCache;
import com.aeroextrem.engine.Core;
import com.aeroextrem.scenario.menu.Menu;
import com.aeroextrem.util.AeroExtrem;
import com.aeroextrem.view.ui.IngameMenu;
import com.aeroextrem.view.ui.OptionsWindow;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import org.jetbrains.annotations.NotNull;

/** Erstellt ein Pause-Menü für die Simulation */
class MenuController {

	private static final String MAIN = "Pause";
	private static final String OPTIONS = "Options";

	private IngameMenu menu;

	MenuController(@NotNull IngameMenu menu) {
		this.menu = menu;
	}

	void createMenu() {
		// Hauptfenster
		PauseWindow main = new PauseWindow();
		main.options.addListener(new WindowSwitchClickListener(OPTIONS));
		main.quit.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Core.getInstance().setScenario(new Menu());
			}
		});
		main.record.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(AeroExtrem.isRecording) {
					main.recordStatus.setText(main.recordOff);
					AeroExtrem.recorder.commit();
					AeroExtrem.isRecording = false;
					AeroExtrem.recorder = null;
				}
				else {
					main.recordStatus.setText(main.recordOn);
					int ID = -1;
					try {
						ID = AeroExtrem.db.createRecording();
					} catch (Exception e) {
						System.err.println("Konnte keinen Rekorder starten!");
						assert false;
					}
					AeroExtrem.recorder = new RecordCache(AeroExtrem.db, ID);
					AeroExtrem.isRecording = true;
				}
			}
		});

		// Optionen-Fenster
		OptionsWindow options = new OptionsWindow();
		options.exit.addListener(new WindowSwitchClickListener(MAIN));

		menu.windows.put(MAIN, main);
		menu.windows.put(OPTIONS, options);

		menu.updateWindowList();
		menu.setVisibleWindow(MAIN);
	}

	/** Listener, der bei Click das Fenster wechselt */
	private final class WindowSwitchClickListener extends ClickListener {
		private final String targetWindow;

		public WindowSwitchClickListener(@NotNull String targetWindow) {
			this.targetWindow = targetWindow;
		}

		@Override
		public void clicked(InputEvent event, float x, float y) {
			menu.setVisibleWindow(targetWindow);
		}
	}

}

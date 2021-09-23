package de.unistuttgart.$MPW_NAME$.viewmodel.impl;

import de.unistuttgart.iste.sqa.mpw.framework.mpw.LogEntry;
import de.unistuttgart.iste.sqa.mpw.framework.datatypes.Size;
import de.unistuttgart.iste.sqa.mpw.framework.mpw.Tile;
import de.unistuttgart.iste.sqa.mpw.framework.viewmodel.ViewModelCell;
import de.unistuttgart.iste.sqa.mpw.framework.viewmodel.ViewModelCellLayer;
import de.unistuttgart.iste.sqa.mpw.framework.viewmodel.ViewModelLogEntry;
import de.unistuttgart.iste.sqa.mpw.framework.viewmodel.impl.GameViewPresenterBase;
import de.unistuttgart.$MPW_NAME$.$MPW_NAME$.*;
import de.unistuttgart.$MPW_NAME$.facade.*;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyObjectProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class $MPW_NAME_FIRST_UPPER$GameViewPresenter extends GameViewPresenterBase {
	private final $STAGE_NAME_FIRST_UPPER$ $STAGE_NAME$;

	private final Map<LogEntry, ViewModelLogEntry> logEntryMap = new HashMap<>();

	public $MPW_NAME_FIRST_UPPER$GameViewPresenter($MPW_NAME_FIRST_UPPER$Game game) {
		super(game);
		this.$STAGE_NAME$ = game.get$STAGE_NAME_FIRST_UPPER$();
	}

	@Override
	protected ReadOnlyObjectProperty<Size> getStageSizeFromConcreteStage() {
		return $STAGE_NAME$.getInternal$STAGE_NAME_FIRST_UPPER$().stageSizeProperty();
	}

	@Override
	protected ReadOnlyListProperty<Tile> getTilesPropertyFromConcreteStage() {
		return $STAGE_NAME$.getInternal$STAGE_NAME_FIRST_UPPER$().tilesProperty();
	}

	@Override
	protected void onSetTileNodeAtForCell(ViewModelCell cell, Tile tile) {
		// TODO: presenter logic
	}

}

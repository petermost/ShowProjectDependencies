package com.pera_software.items;

import com.pera_software.*;
import com.pera_software.aidkit.visualstudio.*;
import javafx.scene.control.*;

public class SolutionTreeItem extends TreeItem< String > {

	public SolutionTreeItem( SolutionFile solutionFile ) {
		super( solutionFile.path().getFileName().toString() );
		setGraphic( Icons.get( Icons.SLN_ICON_NAME ));
		setExpanded( true );
	}
}

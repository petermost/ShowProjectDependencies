package com.pera_software.showprojectdependencies.items;

import com.pera_software.showprojectdependencies.Icons;
import com.pera_software.showprojectdependencies.visualstudio.SolutionFile;
import javafx.scene.control.TreeItem;

public class SolutionTreeItem extends TreeItem< String > {

	public SolutionTreeItem( SolutionFile solutionFile ) {
		super( solutionFile.path().getFileName().toString() );
		setGraphic( Icons.get( Icons.SLN_ICON_NAME ));
		setExpanded( true );
	}
}

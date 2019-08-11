package com.pera_software.showprojectdependencies.items;

import com.pera_software.showprojectdependencies.Icons;
import javafx.scene.control.TreeItem;

public class LibrariesTreeItem extends TreeItem< String > {

	public LibrariesTreeItem() {
		super( "Libraries" );
		setGraphic( Icons.get( Icons.NODE_ICON_NAME ));
	}
}

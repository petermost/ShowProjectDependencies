package com.pera_software.showprojectdependencies.items;

import com.pera_software.showprojectdependencies.Icons;
import javafx.scene.control.TreeItem;

public class LibraryTreeItem extends TreeItem< String > {

	public LibraryTreeItem( String libraryName ) {
		super( libraryName );
		setGraphic( Icons.get( Icons.LIB_ICON_NAME ));
	}
}

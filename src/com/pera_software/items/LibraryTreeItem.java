package com.pera_software.items;

import com.pera_software.*;
import javafx.scene.control.*;

public class LibraryTreeItem extends TreeItem< String > {

	public LibraryTreeItem( String libraryName ) {
		super( libraryName );
		setGraphic( Icons.get( Icons.LIB_ICON_NAME ));
	}
}

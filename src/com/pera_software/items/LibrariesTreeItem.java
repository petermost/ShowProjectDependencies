package com.pera_software.items;

import com.pera_software.*;
import javafx.scene.control.*;

public class LibrariesTreeItem extends TreeItem< String > {

	public LibrariesTreeItem() {
		super( "Libraries" );
		setGraphic( Icons.get( Icons.NODE_ICON_NAME ));
	}
}

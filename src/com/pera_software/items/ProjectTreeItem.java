package com.pera_software.items;

import com.pera_software.*;
import com.pera_software.aidkit.visualstudio.*;
import javafx.scene.control.*;
import javafx.scene.image.*;

public class ProjectTreeItem extends TreeItem< String > {

	public ProjectTreeItem( ProjectFile projectFile ) {
		super( projectFile.path().getFileName().toString() );
		setGraphic( getProjectIcon( projectFile ));
	}

	private static ImageView getProjectIcon( ProjectFile projectFile ) {
		if ( projectFile instanceof CPlusPlusProjectFile )
			return Icons.get( Icons.CPP_ICON_NAME );
		else if ( projectFile instanceof CSharpProjectFile )
			return Icons.get( Icons.CS_ICON_NAME );
		else
			return null;
	}
}

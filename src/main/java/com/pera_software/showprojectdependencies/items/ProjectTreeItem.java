package com.pera_software.showprojectdependencies.items;

import com.pera_software.showprojectdependencies.Icons;
import com.pera_software.showprojectdependencies.visualstudio.CPlusPlusProjectFile;
import com.pera_software.showprojectdependencies.visualstudio.CSharpProjectFile;
import com.pera_software.showprojectdependencies.visualstudio.ProjectFile;
import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;

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

package com.pera_software;

import java.util.*;
import javafx.scene.image.*;

public final class Icons {
	public static final String SLN_ICON_NAME = "icons16x16/visual-studio-solution.png";
	public static final String CPP_ICON_NAME = "icons16x16/text-x-c++src.png";
	public static final String CS_ICON_NAME = "icons16x16/text-x-csharp.png";
	public static final String LIB_ICON_NAME = "icons16x16/code-class.png";
	public static final String NODE_ICON_NAME = "icons16x16/folder-yellow.png";
	public static final String EXIT_ICON_NAME = "icons16x16/application-exit.png";

	private static Map< String, Image > s_icons = new HashMap<>();

	private Icons() {
	}

	public static ImageView get( String iconName ) {
		Image image = s_icons.get( iconName );
		if ( image == null ) {
			image = new Image( Icons.class.getResourceAsStream( iconName ));
			s_icons.put( iconName, image );
		}
		return new ImageView( image );
	}
}

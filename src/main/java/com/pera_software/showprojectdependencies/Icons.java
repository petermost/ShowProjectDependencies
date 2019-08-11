package com.pera_software.showprojectdependencies;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public final class Icons {
	private static final String PACKAGE_NAME = "icons16x16/";

	// The resource names must be valid Java identifiers!!!

	public static final String SLN_ICON_NAME  = PACKAGE_NAME + "visual_studio_solution.png";
	public static final String CPP_ICON_NAME  = PACKAGE_NAME + "text_x_cpp_src.png";
	public static final String CS_ICON_NAME   = PACKAGE_NAME + "text_x_csharp_src.png";
	public static final String LIB_ICON_NAME  = PACKAGE_NAME + "code_class.png";
	public static final String NODE_ICON_NAME = PACKAGE_NAME + "folder_yellow.png";
	public static final String EXIT_ICON_NAME = PACKAGE_NAME + "application_exit.png";

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

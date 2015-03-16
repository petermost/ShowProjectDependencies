package com.pera_software;

import java.nio.file.*;
import javafx.stage.*;
import com.pera_software.aidkit.javafx.application.*;

//##################################################################################################

public class Main extends Application {

	//==============================================================================================

	public static void main( String arguments[] ) {
		Settings.parseCommandLine( arguments );
		launch( arguments );
	}

	//==============================================================================================

	@Override
	public void start( Stage stage ) throws Exception {
		ApplicationMainScene mainWindow = new ApplicationMainScene( stage );
		mainWindow.show();

		mainWindow.showDependencies( Paths.get( Settings.solutionFileNames().get( 0 )));
	}
}

//private void buildSolutionTree2( SolutionFile solutionFile, TreeItem< String > solutionItem ) throws Exception {
//	ExecutorService executor = Executors.newSingleThreadExecutor();
//	Future< Void > builderFuture = executor.submit(() -> {
//		List< ProjectFile > projectFiles = solutionFile.loadProjects();
//		sortByProjectName( projectFiles );
//		for ( ProjectFile projectFile : projectFiles ) {
//			buildProjectTree( solutionFile, solutionItem, projectFile );
//		}
//		Platform.runLater( () -> _statusBar.setText( String.format( "Finished loading %d projects", projectFiles.size() )));
//
//		return null;
//	});
//	builderFuture.get();
//}

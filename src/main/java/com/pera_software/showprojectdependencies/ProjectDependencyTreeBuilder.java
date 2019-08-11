package com.pera_software.showprojectdependencies;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import com.pera_software.aidkit.signal.Signal1;
import com.pera_software.aidkit.signal.Signal2;
import com.pera_software.showprojectdependencies.items.LibrariesTreeItem;
import com.pera_software.showprojectdependencies.items.LibraryTreeItem;
import com.pera_software.showprojectdependencies.items.ProjectTreeItem;
import com.pera_software.showprojectdependencies.items.SolutionTreeItem;
import com.pera_software.showprojectdependencies.visualstudio.ProjectFile;
import com.pera_software.showprojectdependencies.visualstudio.SolutionFile;
import javafx.scene.control.TreeItem;

public class ProjectDependencyTreeBuilder {

	public final Signal2< Integer, Integer > projectLoaded = new Signal2<>();

	public final Signal1< SolutionTreeItem > solutionItemAdded = new Signal1<>();
	public final Signal2< TreeItem< String >, ProjectTreeItem > projectItemAdded = new Signal2<>();
	public final Signal2< ProjectTreeItem, LibrariesTreeItem > librariesItemAdded = new Signal2<>();
	public final Signal2< LibrariesTreeItem, LibraryTreeItem > libraryItemAdded = new Signal2<>();

	public final Signal1< Integer > buildingFinished = new Signal1<>();

	//==============================================================================================

	public void build( Path solutionFilePath ) {
		Thread builder = new Thread(() -> {
			try {
				SolutionFile solutionFile = new SolutionFile( solutionFilePath );
				SolutionTreeItem solutionItem = new SolutionTreeItem( solutionFile );

				solutionItemAdded.emit( solutionItem );
				buildSolutionTree( solutionFile, solutionItem );
			} catch ( Exception exception ) {
				throw new Error( exception );
			}
		});
		builder.start();
	}

	//==============================================================================================

	private void buildSolutionTree( SolutionFile solutionFile, TreeItem< String > solutionItem ) throws Exception {
		List< ProjectFile > projectFiles = solutionFile.loadProjects();
		sortByProjectName( projectFiles );
		for ( int i = 0; i < projectFiles.size(); i++ ) {
			ProjectFile projectFile = projectFiles.get( i );
			buildProjectTree( solutionFile, solutionItem, projectFile );
			projectLoaded.emit( i, projectFiles.size() );
		}
		buildingFinished.emit( projectFiles.size() );
	}

	//==============================================================================================

	private void buildProjectTree( SolutionFile solutionFile, TreeItem< String > parentItem,
		ProjectFile projectFile  ) throws Exception {

		ProjectTreeItem projectItem = new ProjectTreeItem( projectFile );
		projectItemAdded.emit( parentItem, projectItem );

		for ( Path referencedProjectPath : projectFile.getProjectReferences() ) {
			ProjectFile projectReference = solutionFile.findProject( referencedProjectPath );
			buildProjectTree( solutionFile, projectItem, projectReference );
		}
		buildLibraryBranch( projectItem, projectFile.getLibraryReferences() );
	}

	//==============================================================================================

	private void buildLibraryBranch( ProjectTreeItem parentItem, List< String > libraryNames ) throws Exception {
		if ( !libraryNames.isEmpty() ) {
			LibrariesTreeItem librariesItem = new LibrariesTreeItem();
			librariesItemAdded.emit( parentItem, librariesItem );

			for ( String libraryName : libraryNames ) {
				LibraryTreeItem libraryItem = new LibraryTreeItem( libraryName );
				libraryItemAdded.emit( librariesItem, libraryItem );
			}
		}
	}

	//==============================================================================================

	private static void sortByProjectName( List< ProjectFile > projects ) {
		Collections.sort( projects, ( left, right ) -> {
			try {
				String leftFileName = left.path().getFileName().toString();
				String rightFileName = right.path().getFileName().toString();
				return leftFileName.compareTo( rightFileName );
			} catch ( Exception exception ) {
				throw new RuntimeException( exception );
			}
		});
	}

}

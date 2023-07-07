// Amritesh Dasgupta, Ruitong Wang, Vincent Luu
// fri11-00-team-07

package src.mapeditor.editor;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import src.Driver;
import src.Game;
import src.checkers.levelChecker.LevelChecker;
import src.mapeditor.editor.levelHandler.DirectoryLevelHandler;
import src.mapeditor.editor.levelHandler.FileLevelHandler;
import src.mapeditor.editor.levelHandler.LevelHandler;
import src.mapeditor.grid.*;
import src.mapeditor.editor.levelHandler.NullLevelHandler;
import src.utility.GameCallback;
import src.utility.PropertiesLoader;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Properties;


/**
 * Controller of the application.
 *
 * @author Daniel "MaTachi" Jonsson
 * @version 1
 * @since v0.0.5
 *
 */
public class Controller implements ActionListener, GUIInformation {

	/**
	 * The model of the map editor.
	 */

	public final String DATA_PATH = "src/data/";
	private Grid model;

	private File curLevel;
	private Tile selectedTile;
	private Camera camera;

	private List<Tile> tiles;

	private GridView grid;
	private View view;

	private int gridWith = Constants.MAP_WIDTH;
	private int gridHeight = Constants.MAP_HEIGHT;
	private final LevelChecker checker = new LevelChecker();



	/**
	 * Construct the controller.
	 */
	public Controller(File level) {
		this.curLevel = level;
		LevelHandler levelHandler;
		if(level == null){
			levelHandler = new NullLevelHandler();
		}else if (level.isDirectory()) {
			levelHandler = new DirectoryLevelHandler();
		} else {
			levelHandler = new FileLevelHandler();
		}
		levelHandler.handleLevel(this, level);

	}



	public void init(int width, int height) {
		this.tiles = TileManager.getTilesFromFolder(DATA_PATH);
		this.model = new GridModel(width, height, tiles.get(0).getCharacter());
		this.camera = new GridCamera(model, Constants.GRID_WIDTH,
				Constants.GRID_HEIGHT);

		this.grid = new GridView(this, camera, tiles); // Every tile is
		// 30x30 pixels

		this.view = new View(this, camera, grid, tiles);
	}

	/**
	 * Different commands that comes from the view.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		for (Tile t : tiles) {
			if (e.getActionCommand().equals(
					Character.toString(t.getCharacter()))) {
				selectedTile = t;
				break;
			}
		}
		if (e.getActionCommand().equals("flipGrid")) {
			// view.flipGrid();
		} else if (e.getActionCommand().equals("save")) {
			saveFile();
		} else if (e.getActionCommand().equals("load")) {
			loadFile();
		} else if (e.getActionCommand().equals("update")) {
			updateGrid(gridWith, gridHeight);
		} else if (e.getActionCommand().equals("start_game")) {
			startGame();
		}
	}

	public void updateGrid(int width, int height) {
		view.close();
		init(width, height);
		setCurLevel(null); // This is an edge case - What happens if we reset after choosing a file
		view.setSize(width, height);
	}

	DocumentListener updateSizeFields = new DocumentListener() {

		public void changedUpdate(DocumentEvent e) {
			gridWith = view.getWidth();
			gridHeight = view.getHeight();
		}

		public void removeUpdate(DocumentEvent e) {
			gridWith = view.getWidth();
			gridHeight = view.getHeight();
		}

		public void insertUpdate(DocumentEvent e) {
			gridWith = view.getWidth();
			gridHeight = view.getHeight();
		}
	};

	private void saveFile() {

		JFileChooser chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"xml files", "xml");
		chooser.setFileFilter(filter);
		File workingDirectory = new File(System.getProperty("user.dir"));
		chooser.setCurrentDirectory(workingDirectory);

		int returnVal = chooser.showSaveDialog(null);
		try {
			if (returnVal == JFileChooser.APPROVE_OPTION) {

				Element level = new Element("level");
				Document doc = new Document(level);
				doc.setRootElement(level);

				Element size = new Element("size");
				int height = model.getHeight();
				int width = model.getWidth();
				size.addContent(new Element("width").setText(width + ""));
				size.addContent(new Element("height").setText(height + ""));
				doc.getRootElement().addContent(size);

				for (int y = 0; y < height; y++) {
					Element row = new Element("row");
					for (int x = 0; x < width; x++) {
						char tileChar = model.getTile(x,y);
						String type = "PathTile";

						if (tileChar == 'b')
							type = "WallTile";
						else if (tileChar == 'c')
							type = "PillTile";
						else if (tileChar == 'd')
							type = "GoldTile";
						else if (tileChar == 'e')
							type = "IceTile";
						else if (tileChar == 'f')
							type = "PacTile";
						else if (tileChar == 'g')
							type = "TrollTile";
						else if (tileChar == 'h')
							type = "TX5Tile";
						else if (tileChar == 'i')
							type = "PortalWhiteTile";
						else if (tileChar == 'j')
							type = "PortalYellowTile";
						else if (tileChar == 'k')
							type = "PortalDarkGoldTile";
						else if (tileChar == 'l')
							type = "PortalDarkGrayTile";

						Element e = new Element("cell");
						row.addContent(e.setText(type));
					}
					doc.getRootElement().addContent(row);
				}
				XMLOutputter xmlOutput = new XMLOutputter();
				xmlOutput.setFormat(Format.getPrettyFormat());
				xmlOutput
						.output(doc, new FileWriter(chooser.getSelectedFile()));
				// Check map
				checker.checkLevel(String.valueOf(chooser.getSelectedFile()));
			}
		} catch (FileNotFoundException e1) {
			JOptionPane.showMessageDialog(null, "Invalid file!", "error",
					JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
		}
	}

	public void loadFile(File selectedFile) {
		if(selectedFile == null){
			return;
		}
		curLevel = selectedFile;
		SAXBuilder builder = new SAXBuilder();
		try {
			Document document = builder.build(selectedFile);

			Element rootNode = document.getRootElement();

			List rows = rootNode.getChildren("row");
			for (int y = 0; y < rows.size(); y++) {
				Element cellsElem = (Element) rows.get(y);
				List cells = cellsElem.getChildren("cell");

				for (int x = 0; x < cells.size(); x++) {
					Element cell = (Element) cells.get(x);
					String cellValue = cell.getText();

					char tileNr = 'a';
					if (cellValue.equals("PathTile"))
						tileNr = 'a';
					else if (cellValue.equals("WallTile"))
						tileNr = 'b';
					else if (cellValue.equals("PillTile"))
						tileNr = 'c';
					else if (cellValue.equals("GoldTile"))
						tileNr = 'd';
					else if (cellValue.equals("IceTile"))
						tileNr = 'e';
					else if (cellValue.equals("PacTile"))
						tileNr = 'f';
					else if (cellValue.equals("TrollTile"))
						tileNr = 'g';
					else if (cellValue.equals("TX5Tile"))
						tileNr = 'h';
					else if (cellValue.equals("PortalWhiteTile"))
						tileNr = 'i';
					else if (cellValue.equals("PortalYellowTile"))
						tileNr = 'j';
					else if (cellValue.equals("PortalDarkGoldTile"))
						tileNr = 'k';
					else if (cellValue.equals("PortalDarkGrayTile"))
						tileNr = 'l';
					else
						tileNr = '0';

					model.setTile(x, y, tileNr);
				}
			}

			String mapString = model.getMapAsString();
			grid.redrawGrid();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadFile() {
		JFileChooser chooser = new JFileChooser();
		File workingDirectory = new File(System.getProperty("user.dir"));
		chooser.setCurrentDirectory(workingDirectory);

		int returnVal = chooser.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File selectedFile = chooser.getSelectedFile();
			this.curLevel = selectedFile;
			loadFile(selectedFile);
		}
	}


	public void startGame() {
		String propertiesPath = Driver.DEFAULT_PROPERTIES_PATH;
		final Properties properties = PropertiesLoader.loadPropertiesFile(propertiesPath);
		GameCallback gameCallback = new GameCallback();

		Thread gameThread = new Thread(() -> {
			if (view != null) {
				view.close();
			}
			Game game;
			try {
				game = new Game(gameCallback, properties, curLevel);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

			if (!game.running()) {
				game.hide();
				if (view == null && game.curFile() == null) {
					/** lazy creation saves startup time as we are not intializing till it's actually needed **/
					init(Constants.MAP_WIDTH, Constants.MAP_HEIGHT);
				} else {
					if(view == null){
						init(Constants.MAP_WIDTH, Constants.MAP_HEIGHT);
						loadFile(game.curFile());
					}
					view.reopen();
				}
			}

		});
		gameThread.start();

	}

	public File curLevel() {
		return curLevel;
	}

	public Controller setCurLevel(File curLevel) {
		this.curLevel = curLevel;
		return this;
	}


		/**
         * {@inheritDoc}
         */
	@Override
	public Tile getSelectedTile() {
		return selectedTile;
	}
}

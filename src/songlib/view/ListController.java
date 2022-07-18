package songlib.view;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.FileWriter;

public class ListController {

	@FXML
	ListView<String> listView;

	@FXML
	Button AddButton, EditButton, DeleteButton;
	@FXML
	TextField TitleId, AlbumId, YearId, ArtistId;
	@FXML
	public Label songDetails;

	private ObservableList<String> obsList = FXCollections.observableArrayList();
	private ArrayList<Song> songList = new ArrayList<Song>();

	public void start(Stage primaryStage) throws IOException {
		// create an ObservableList
		// from an ArrayList

		// 1.load the songs from the file
		File myFile = null;

		try {

			myFile = new File("songLibrary.txt");
			myFile.createNewFile();

			Scanner sc = new Scanner(myFile);
			while (sc.hasNextLine()) {
				String data = sc.nextLine();
				// System.out.println(data);
				String[] dataArr = data.split("@");
				// System.out.println(dataArr[0] + dataArr[1] + dataArr[2] + dataArr[3]);
				Song s = new Song(dataArr[0], dataArr[1], dataArr[2], Integer.parseInt(dataArr[3]));
				songList.add(s);

			}
			sc.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
		// 2. Add the current Songs on to the Listview
		for (int i = 0; i < songList.size(); i++) {
			obsList.add(songList.get(i).getTitle() + ", " + songList.get(i).getArtist());
		}
		listView.setItems(obsList);
		// 3. Pre select the first item to make it show or don't show anything if empty
		listView.getSelectionModel().select(0);
		String currSong = listView.getSelectionModel().getSelectedItem();
		if (!(currSong == null || currSong.isEmpty())) {
			songDetails.setText(songList.get(0).getTitle() + "\n" + songList.get(0).getArtist() + "\n"
					+ songList.get(0).getAlbum() + "\n" + songList.get(0).getArtist());
		} else {
			songDetails.setText("");
		}
		listView.getSelectionModel().selectedItemProperty()
				.addListener((obs, oldVal, newVal) -> changeDetails(primaryStage));

	}

	private void changeDetails(Stage primaryStage) {
		String item = listView.getSelectionModel().getSelectedItem();
		int index = listView.getSelectionModel().getSelectedIndex();

		if (!(item == null || item.isEmpty())) {
			songDetails.setText(songList.get(index).getTitle() + "\n" + songList.get(index).getArtist() + "\n"
					+ songList.get(index).getAlbum() + "\n" + songList.get(index).getYear());
		} else {
			songDetails.setText("");
		}
	}

	public void onButtonAction(ActionEvent e) {
		Button b = (Button) e.getSource();

		if (b == AddButton) {
			String title = TitleId.getText().trim().toLowerCase();
			String artist = ArtistId.getText().trim().toLowerCase();
			String album = AlbumId.getText().trim().toLowerCase();
			String year = YearId.getText().trim();
			int yearInNumber = 0;
			boolean validYear = true;
			try {
				yearInNumber = Integer.parseInt(year);
				validYear = false;
			} catch (NumberFormatException e1) {
				validYear = true;
			}
			boolean ifAlreadyExists = false;

			for (int i = 0; i < songList.size(); i++) {
				if (songList.get(i).getTitle().equals(title)) {
					if (songList.get(i).getArtist().equals(artist)) {
						ifAlreadyExists = true;
					}
				}
			}

			if (year == null || year.isEmpty()) {
				yearInNumber = 0;
				validYear = false;
			}

			// if title and artist are not empty or null or "If the name and artist are the
			// same as an existing song"
			if (title == null || title.isEmpty() || artist == null || artist.isEmpty() || ifAlreadyExists
					|| validYear) {
				Alert alert = new Alert(AlertType.ERROR);
				// alert.initOwner(primaryStage);
				alert.setTitle("Error");
				alert.setHeaderText("Invalid Title or Artist or Year");
				alert.setContentText("Please enter valid Title and Artist...");
				alert.showAndWait();

			} else {
				Alert alert = new Alert(AlertType.CONFIRMATION);
				// alert.initOwner(primaryStage);
				alert.setTitle("Confirmation");
				alert.setHeaderText("Valid Title and Artist");
				alert.setContentText("Please Confirm your action...");
				alert.showAndWait();

				if (alert.getResult() != ButtonType.CANCEL) {
					if (album == null || album.isEmpty()) {
						album = "null";
					}
					Song newSong = new Song(title, artist, album, yearInNumber);
					// add song into arraylist and listview

					int index = -1;
					for (int i = 0; i < songList.size(); i++) {
						if (songList.get(i).getTitle().compareTo(newSong.getTitle()) > 0) {
							songList.add(i, newSong);
							index = i;
							break;
						} else if (songList.get(i).getTitle().compareTo(newSong.getTitle()) == 0) {
							if (songList.get(i).getArtist().compareTo(newSong.getArtist()) > 0) {
								songList.add(i, newSong);
								index = i;
								break;
							}
						}
					}
					if (index == -1) {
						songList.add(newSong);
						index = songList.indexOf(newSong);
					}
					// change listview
					obsList.add(index, newSong.getTitle() + ", " + newSong.getArtist());
					listView.getSelectionModel().select(index);
					// add the .txt file

					updateFile();

					// clear the textfields
					TitleId.clear();
					ArtistId.clear();
					YearId.clear();
					AlbumId.clear();

				}

			}

		} else if (b == EditButton) {

			// delete the song from the library
			// then add the new updated song
			String item = listView.getSelectionModel().getSelectedItem();
			int index = listView.getSelectionModel().getSelectedIndex();

			String title = TitleId.getText().trim().toLowerCase();
			String artist = ArtistId.getText().trim().toLowerCase();
			String album = AlbumId.getText().trim().toLowerCase();
			String year = YearId.getText().trim();

			if (title == null || title.isEmpty()) {
				title = songList.get(index).getTitle();
			}
			if (artist == null || artist.isEmpty()) {
				artist = songList.get(index).getArtist();
			}
			if (album == null || album.isEmpty()) {
				album = songList.get(index).getAlbum();
			}

			int yearInNumber = 0;
			boolean validYear = true;
			try {
				yearInNumber = Integer.parseInt(year);
				validYear = false;
			} catch (NumberFormatException e1) {
				validYear = true;
			}
			boolean ifAlreadyExists = false;

			// if(songList.get(index).getTitle().equals(title))

			for (int i = 0; i < songList.size(); i++) {
				if (songList.get(i).getTitle().equals(title)) {
					if (songList.get(i).getArtist().equals(artist)) {
						if (i != index) {
							ifAlreadyExists = true;
						}
					}
				}
			}

			if (year == null || year.isEmpty()) {
				yearInNumber = songList.get(index).getYear();
				validYear = false;
			}

			// if title and artist are not empty or null or "If the name and artist are the
			// same as an existing song"
			if (ifAlreadyExists || validYear) {
				Alert alert = new Alert(AlertType.ERROR);
				// alert.initOwner(primaryStage);
				alert.setTitle("Error");
				alert.setHeaderText("Invalid Title or Artist or Year");
				alert.setContentText("Please enter valid Title and Artist...");
				alert.showAndWait();

			} else {

				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Confirmation");
				alert.setHeaderText("Valid Title and Artist");
				alert.setContentText("Please Confirm your action...");
				alert.showAndWait();

				if (alert.getResult() != ButtonType.CANCEL) {
					// delete the current song from the library
					if (index != -1) {
						final int newSong = (index == listView.getItems().size() - 1) ? index - 1 : index;
						// listView.getItems().remove(index);
						songList.remove(index);
						obsList.remove(index);
						listView.getSelectionModel().select(newSong);
					}

					// add new song into the lists

					Song newSong = new Song(title, artist, album, yearInNumber);
					// add song into arraylist and listview

					int currIndex = -1;
					for (int i = 0; i < songList.size(); i++) {
						if (songList.get(i).getTitle().compareTo(newSong.getTitle()) > 0) {
							songList.add(i, newSong);
							currIndex = i;
							break;
						} else if (songList.get(i).getTitle().compareTo(newSong.getTitle()) == 0) {
							if (songList.get(i).getArtist().compareTo(newSong.getArtist()) > 0) {
								songList.add(i, newSong);
								currIndex = i;
								break;
							}
						}
					}
					if (currIndex == -1) {
						songList.add(newSong);
						currIndex = songList.indexOf(newSong);
					}
					// change listview
					obsList.add(currIndex, newSong.getTitle() + ", " + newSong.getArtist());
					listView.getSelectionModel().select(currIndex);
					// add the .txt file

					updateFile();

					// clear the textfields
					TitleId.clear();
					ArtistId.clear();
					YearId.clear();
					AlbumId.clear();

				}

			}

		} else if (b == DeleteButton) {
			String currSong = listView.getSelectionModel().getSelectedItem();
			int index = listView.getSelectionModel().getSelectedIndex();
			if (index != -1) {

				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Confirmation");
				alert.setHeaderText("Please Confirm");
				alert.setContentText("Please Confirm to delete...");
				alert.showAndWait();

				if (alert.getResult() != ButtonType.CANCEL) {
					// makes sure that the songlist is not empty
					if (index != -1) {
						final int newSong = (index == listView.getItems().size() - 1) ? index - 1 : index;
						// listView.getItems().remove(index);
						songList.remove(index);
						obsList.remove(index);
						listView.getSelectionModel().select(newSong);

						updateFile();
					}
				}
			}
		}
	}

	public void updateFile() {

		try {
			FileWriter myWriter = new FileWriter("songLibrary.txt");

			for (int i = 0; i < songList.size(); i++) {
				myWriter.write(songList.get(i).getTitle() + "@" + songList.get(i).getArtist() + "@"
						+ songList.get(i).getAlbum() + "@" + songList.get(i).getYear() + "\n");
			}

			// myWriter.write("Files in Java might be tricky, but it is fun enough!");

			myWriter.close();
			// System.out.println("Successfully wrote to the file.");
		} catch (IOException e) {
			// System.out.println("An error occurred.");
			e.printStackTrace();
		}

	}

}
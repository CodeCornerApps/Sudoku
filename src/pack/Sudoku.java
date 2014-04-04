package pack;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

@SuppressWarnings("serial")
public class Sudoku extends JFrame {
	private MiniGrid[][] grids;
	private boolean stopTime = false;
	private int timer = 0;
	private int seconds = 0;
	private int minutes = 0;
	private int hours = 0;
	private JLabel time = new JLabel();
	int[][] savedgrid = new int[9][9];

	public Sudoku() {
		super("Sudoku by Mark Robinson");

		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension dim = toolkit.getScreenSize();
		this.setBounds((dim.width * 29) / 100, (dim.height * 29) / 100,
				(dim.width * 42) / 100, (dim.height * 42) / 100);
		JPanel board = new JPanel(new GridLayout(3, 3));
		grids = new MiniGrid[3][3];

		for (int xx = 0; xx < 3; xx++) {
			for (int yy = 0; yy < 3; yy++) {
				MiniGrid g = new MiniGrid(this);
				g.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2));
				grids[xx][yy] = g;
				board.add(g);
			}
		}

		JPanel bottomlow = new JPanel(new GridLayout(1, 0));

		JButton makerandgrid = new JButton();
		makerandgrid.setText("Make Grid");
		makerandgrid.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				resetboard();
				makeRandomGrid();
			}

		});

		JButton clear = new JButton();
		clear.setText("Clear Grid");
		clear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				resetboard();
			}
		});

		JButton solveforme = new JButton();
		solveforme.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int n = 3;
				for (int i = 0; i < n * n; i++) {
					for (int j = 0; j < n * n; j++) {
						getAbsButton(i, j).setText(
								String.valueOf(savedgrid[i][j]));
					}
				}
				validateGrid();
			}
		});

		solveforme.setText("Solve It For Me");

		makeRandomGrid();
		time.setText("Elapsed TI:ME");
		time.setHorizontalAlignment(SwingConstants.CENTER);
		bottomlow.add(makerandgrid);
		bottomlow.add(clear);
		bottomlow.add(solveforme);
		bottomlow.add(time);

		this.add(board);
		this.add(bottomlow, BorderLayout.SOUTH);
		this.setVisible(true);
		this.getContentPane().revalidate();
		this.repaint();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

	public JButton getButton(int gridx, int gridy, int minix, int miniy) {
		return grids[gridx][gridy].getButton(minix, miniy);
	}

	public JButton getAbsButton(int x, int y) {

		int minix = (int) x / 3;
		int miniy = (int) y / 3;

		MiniGrid n = grids[minix][miniy];

		int buttonx = x - (3 * minix);
		int buttony = y - (3 * miniy);

		return n.getButton(buttonx, buttony);
	}

	public void resetboard() {
		time.setText("Elapsed TI:ME");
		timer = 0;
		stopTime = false;
		for (int mbx = 0; mbx < 9; mbx++) {
			for (int mby = 0; mby < 9; mby++) {
				JButton foundbutton = getAbsButton(mbx, mby);
				foundbutton.setText("");
				foundbutton.setFocusable(true);
				foundbutton.setToolTipText("");
			}
		}
		validateGrid();
	}

	public boolean checkWin() {

		for (int mbx = 0; mbx < 9; mbx++) {
			for (int mby = 0; mby < 9; mby++) {
				JButton foundbutton = getAbsButton(mbx, mby);
				if (foundbutton.getText().equals("")
						|| foundbutton.getBackground().equals(Color.RED)) {
					return false;
				}
			}
		}
		stopTime = true;
		int j = JOptionPane.showConfirmDialog(null,
				"You completed the board in " + hours + ":" + minutes + ":"
						+ seconds + " seconds! Play again?");
		if (j == 0) {
			resetboard();
		}
		return true;
	}

	public void validateGrid() {
		ArrayList<Integer> usedintegers = new ArrayList<Integer>();

		/**
		 * Check grids for duplicate integers.
		 */

		for (int minigx = 0; minigx < 3; minigx++) {
			for (int minigy = 0; minigy < 3; minigy++) {
				usedintegers.clear();
				for (int mbx = 0; mbx < 3; mbx++) {
					for (int mby = 0; mby < 3; mby++) {
						JButton foundbutton = getButton(minigx, minigy, mbx,
								mby);
						try {
							usedintegers.add(Integer.valueOf(foundbutton
									.getText()));
						} catch (NumberFormatException n) {
						}
					}
				}

				for (int mbx = 0; mbx < 3; mbx++) {
					for (int mby = 0; mby < 3; mby++) {

						JButton foundbutton = getButton(minigx, minigy, mbx,
								mby);
						int duplicatefound = 0;
						for (int uni : usedintegers) {

							try {
								if (uni == Integer.valueOf(foundbutton
										.getText())) {
									duplicatefound = duplicatefound + 1;
								}
							} catch (NumberFormatException n) {
							}
						}
						if (duplicatefound > 1) {
							foundbutton.setBackground(Color.RED);
						} else {
							foundbutton.setBackground(UIManager
									.getColor("Button.background"));
						}
					}
				}
				usedintegers.clear();
			}
		}
		/**
		 * Check rows for duplicate integers.
		 */

		validaterows();

		/**
		 * Check columns for duplicate integers.
		 */

		validatecolms();

		/**
		 * Check buttons for green coloring.
		 */

		checkGreen();

		/**
		 * Check if the user completed a board.
		 */
		checkWin();
	}

	private void checkGreen() {

		/**
		 * Check Grids; green coloring
		 */

		for (int minigx = 0; minigx < 3; minigx++) {
			for (int minigy = 0; minigy < 3; minigy++) {
				boolean gridgreen = true;
				for (int mbx = 0; mbx < 3; mbx++) {
					for (int mby = 0; mby < 3; mby++) {
						JButton foundbutton = getButton(minigx, minigy, mbx,
								mby);
						if (foundbutton.getBackground().equals(Color.RED)
								|| foundbutton.getText() == "") {
							gridgreen = false;
						}
					}
				}
				if (gridgreen) {
					for (int mbx = 0; mbx < 3; mbx++) {
						for (int mby = 0; mby < 3; mby++) {
							JButton foundbutton = getButton(minigx, minigy,
									mbx, mby);
							foundbutton.setBackground(Color.GREEN);
						}
					}
				}
			}
		}

		/**
		 * Check Rows; green coloring
		 */

		for (int row = 0; row < 9; row++) {
			boolean gridgreen = true;
			for (int bu = 0; bu < 9; bu++) {
				JButton c = getAbsButton(row, bu);
				if (c.getBackground().equals(Color.RED) || c.getText() == "") {
					gridgreen = false;
				}
			}
			if (gridgreen) {
				for (int bu = 0; bu < 9; bu++) {
					JButton c = getAbsButton(row, bu);
					c.setBackground(Color.GREEN);
				}
			}
		}

		/**
		 * Check Columns; green coloring
		 */
		for (int col = 0; col < 9; col++) {
			boolean gridgreen = true;
			for (int row = 0; row < 9; row++) {
				JButton c = getAbsButton(row, col);
				if (c.getBackground().equals(Color.RED) || c.getText() == "") {
					gridgreen = false;
				}
			}
			if (gridgreen) {
				for (int row = 0; row < 9; row++) {
					JButton c = getAbsButton(row, col);
					c.setBackground(Color.GREEN);
				}
			}
		}

	}

	public void validatecolms() {
		ArrayList<Integer> usedintegers = new ArrayList<Integer>();
		for (int col = 0; col < 9; col++) {
			usedintegers.clear();
			for (int row = 0; row < 9; row++) {
				JButton c = getAbsButton(row, col);
				try {
					usedintegers.add(Integer.valueOf(c.getText()));
				} catch (NumberFormatException n) {
				}
			}

			for (int row = 0; row < 9; row++) {
				JButton c = getAbsButton(row, col);
				int duplicatefound = 0;
				for (int uni : usedintegers) {
					try {
						if (uni == Integer.valueOf(c.getText())) {
							duplicatefound = duplicatefound + 1;
						}
					} catch (NumberFormatException n) {
					}
				}
				if (duplicatefound > 1) {
					c.setBackground(Color.RED);
				}
			}
		}
		usedintegers.clear();
	}

	public void validaterows() {
		ArrayList<Integer> usedintegers = new ArrayList<Integer>();
		for (int row = 0; row < 9; row++) {
			usedintegers.clear();
			for (int bu = 0; bu < 9; bu++) {
				JButton c = getAbsButton(row, bu);
				try {
					usedintegers.add(Integer.valueOf(c.getText()));
				} catch (NumberFormatException n) {
				}

			}
			for (int bu = 0; bu < 9; bu++) {
				JButton c = getAbsButton(row, bu);
				int duplicatefound = 0;
				for (int uni : usedintegers) {

					try {
						if (uni == Integer.valueOf(c.getText())) {
							duplicatefound = duplicatefound + 1;
						}
					} catch (NumberFormatException n) {
					}
				}
				if (duplicatefound > 1) {
					c.setBackground(Color.RED);
				} else {
					if (c.getBackground().getRed() != 255) {
						c.setBackground(UIManager.getColor("Button.background"));
					}
				}
			}
			usedintegers.clear();
		}
	}

	private boolean isPossibleX(int[][] game, int y, int number) {
		for (int x = 0; x < 9; x++) {
			if (game[y][x] == number)
				return false;
		}
		return true;
	}

	private boolean isPossibleY(int[][] game, int x, int number) {
		for (int y = 0; y < 9; y++) {
			if (game[y][x] == number)
				return false;
		}
		return true;
	}

	private boolean isPossibleBlock(int[][] game, int x, int y, int number) {
		int x1 = x < 3 ? 0 : x < 6 ? 3 : 6;
		int y1 = y < 3 ? 0 : y < 6 ? 3 : 6;
		for (int yy = y1; yy < y1 + 3; yy++) {
			for (int xx = x1; xx < x1 + 3; xx++) {
				if (game[yy][xx] == number)
					return false;
			}
		}
		return true;
	}

	private int getNextPossibleNumber(int[][] game, int x, int y,
			List<Integer> numbers) {
		while (numbers.size() > 0) {
			int number = numbers.remove(0);
			if (isPossibleX(game, y, number) && isPossibleY(game, x, number)
					&& isPossibleBlock(game, x, y, number))
				return number;
		}
		return -1;
	}

	private boolean isValid(int[][] game, int index, int[] numberOfSolutions) {
		if (index > 80)
			return ++numberOfSolutions[0] == 1;

		int x = index % 9;
		int y = index / 9;

		if (game[y][x] == 0) {
			List<Integer> numbers = new ArrayList<Integer>();
			for (int i = 1; i <= 9; i++)
				numbers.add(i);

			while (numbers.size() > 0) {
				int number = getNextPossibleNumber(game, x, y, numbers);
				if (number == -1)
					break;
				game[y][x] = number;

				if (!isValid(game, index + 1, numberOfSolutions)) {
					game[y][x] = 0;
					return false;
				}
				game[y][x] = 0;
			}
		} else if (!isValid(game, index + 1, numberOfSolutions))
			return false;

		return true;
	}

	private boolean isValid(int[][] game) {
		return isValid(game, 0, new int[] { 0 });
	}

	private int[][] generateGame(int[][] game, List<Integer> positions) {
		while (positions.size() > 0) {
			int position = positions.remove(0);
			int x = position % 9;
			int y = position / 9;
			int temp = game[y][x];

			if (!isValid(game))
				game[y][x] = temp;
		}

		return game;
	}

	private int[][] generateGame(int[][] game) {
		List<Integer> positions = new ArrayList<Integer>();
		for (int i = 0; i < 81; i++)
			positions.add(i);
		Collections.shuffle(positions);
		return generateGame(game, positions);
	}

	private int[][] generateSolution(int[][] game, int index) {
		if (index > 80)
			return game;

		int x = index % 9;
		int y = index / 9;

		List<Integer> numbers = new ArrayList<Integer>();
		for (int i = 1; i <= 9; i++)
			numbers.add(i);
		Collections.shuffle(numbers);

		while (numbers.size() > 0) {
			int number = getNextPossibleNumber(game, x, y, numbers);
			if (number == -1)
				return null;

			game[y][x] = number;
			int[][] tmpGame = generateSolution(game, index + 1);
			if (tmpGame != null)
				return tmpGame;
			game[y][x] = 0;
		}

		return null;
	}

	private int[][] copy(int[][] game) {
		int[][] copy = new int[9][9];
		for (int y = 0; y < 9; y++) {
			for (int x = 0; x < 9; x++)
				copy[y][x] = game[y][x];
		}
		return copy;
	}

	public void makeRandomGrid() {
		String typeddif = JOptionPane
				.showInputDialog(
						null,
						"Type ''Easy'' for an easy grid. Type ''Medium'' for a somewhat difficult grid. Type ''Hard'' for a hard grid. Leave blank to make your own grid.");
		final int n = 3;

		int[][] solution = generateSolution(new int[9][9], 0);

		int[][] game = generateGame(copy(solution));
		for (int row = 0; row < 9; row++) {
			for (int bu = 0; bu < 9; bu++) {

				savedgrid[row][bu] = solution[row][bu];

				if (game[row][bu] == 0) {
					getAbsButton(row, bu).setText("");
				} else {
					getAbsButton(row, bu)
							.setText(String.valueOf(game[row][bu]));
				}
			}
		}

		double randomizer = 0;

		if (typeddif == null) {
			for (int i = 0; i < n * n; i++) {
				for (int j = 0; j < n * n; j++) {

					JButton c = getAbsButton(i, j);
					c.setText("");
					c.setFocusable(true);
					c.setToolTipText(null);
				}
			}
		} else {
			if (typeddif.equalsIgnoreCase("easy")) {
				randomizer = 0.7;
			} else {
				if (typeddif.equalsIgnoreCase("medium")) {
					randomizer = 0.5;
				} else {
					if (typeddif.equalsIgnoreCase("hard")) {
						randomizer = 0.3;
					}
				}
			}
		}

		for (int i = 0; i < n * n; i++) {
			for (int j = 0; j < n * n; j++) {
				JButton c = getAbsButton(i, j);

				if (Math.random() > randomizer) {
					c.setText("");
					c.setFocusable(true);
					c.setToolTipText(null);
				} else {
					c.setFocusable(false);
					c.setToolTipText("This button was preset. You can not change this value.");
				}
			}
		}
		validateGrid();

	}

	public void StartTimer() {
		Timer ti = new Timer();
		TimerTask task = new TimerTask() {
			public void run() {
				if (!stopTime) {
					timer++;
					seconds = timer;
					minutes = 0;
					hours = 0;
					for (int rep = 0; rep < 300; rep++) {
						if (seconds > 59) {
							minutes++;
							seconds = seconds - 60;
						}

						if (minutes > 59) {
							hours++;
							minutes = minutes - 60;

						}

						time.setText("Elapsed " + hours + ":" + minutes + ":"
								+ seconds);
					}
				}
			}
		};
		ti.scheduleAtFixedRate(task, 0, 1000);
	}
}
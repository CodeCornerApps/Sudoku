package pack;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class MiniGrid extends JPanel {

	private JButton[][] buttons;

	@SuppressWarnings("unused")
	private Sudoku parent;

	public MiniGrid(final Sudoku parent) {
		super(new GridLayout(3, 3));
		this.parent = parent;
		buttons = new JButton[3][3];

		final Timer t = new Timer();

		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				final JButton button = new JButton();
				buttons[x][y] = button;
				button.setText("");
				button.setFocusPainted(false);
				button.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent arg0) {
						if (button.isFocusable()) {
							try {
								final String input = JOptionPane
										.showInputDialog("Enter a number less than 9 and more than 0");
								if (Double.parseDouble(input) != 0) {
									button.setText(input.substring(0, 1));
									parent.validateGrid();
									if (button.getBackground() == Color.RED) {
										TimerTask task = new TimerTask() {
											public void run() {
												if (button.getText().equals(input)) {
													button.setText("");
													parent.validateGrid();
												}
											}
										};
										t.schedule(task, 1000);
									}
								}
							} catch (NumberFormatException o) {
							}
						}

					}

				});

				button.addMouseMotionListener(new MouseMotionListener() {
					public void mouseMoved(MouseEvent arg0) {
						button.requestFocus();
					}

					public void mouseDragged(MouseEvent e) {
					}
				});

				button.addKeyListener(new KeyListener() {

					@Override
					public void keyPressed(KeyEvent e) {
						try {
							if (Double.parseDouble(String.valueOf(e
									.getKeyChar())) != 0) {
								button.setText(String.valueOf(e.getKeyChar()));
								parent.validateGrid();
								final String old = String.valueOf(e.getKeyChar());
								if (button.getBackground() == Color.RED) {
									TimerTask task = new TimerTask() {
										public void run() {
											if (button.getText().equals(old)) {
												button.setText("");
												parent.validateGrid();
											}
										}
									};
									t.schedule(task, 1000);
								}
							}
						} catch (NumberFormatException o) {

						}
					}

					@Override
					public void keyReleased(KeyEvent e) {

					}

					@Override
					public void keyTyped(KeyEvent e) {

					}

				});
				this.add(button);
				Font newButtonFont = new Font("Tahoma", button.getFont()
						.getStyle(), button.getFont().getSize());
				button.setFont(newButtonFont);
			}
		}
		this.setBackground(Color.BLACK);
	}

	public JButton getButton(int x, int y) {
		return buttons[x][y];
	}
}

package org.example;

import org.joda.time.Interval;
import org.joda.time.LocalDateTime;
import org.joda.time.Period;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

public class OffWorkClock extends JFrame {

	private JLabel timeLabel0;
	private JLabel timeLabel;
	private JLabel timeLabel1;
	private JLabel settingLabel1;
	private JLabel settingLabel2;
	private JButton startButton;
	private JButton settingButton;
	private JTextArea textArea;
	private JTextArea settingTextArea1;
	private JTextArea settingTextArea2;
	private Timer timer;

	private static final String TODAY = LocalDateTime.now().toString(DateUtils.DATE_PATTERN) + " ";
	private static final String TIME_FORMAT = "HH:mm:ss";

	/**
	 * 灵活上班时间时间： 8：30 - 8：:45
	 */
	private static String ON_DUTY_START_SETTING_STR = "08:30:00";
	private static String ON_DUTY_END_SETTING_STR = "08:45:00";

	private static final Date ON_DUTY_START_SETTING = DateUtils.stringToDate(TODAY + ON_DUTY_START_SETTING_STR, DateUtils.DATE_TIME_PATTERN);
	private static final Date ON_DUTY_END_SETTING = DateUtils.stringToDate(TODAY + ON_DUTY_END_SETTING_STR, DateUtils.DATE_TIME_PATTERN);

	/**
	 * 上班时长（小时）
	 */
	private static final Integer ON_DUTY_LASTING_HOUR_SETTING = 8;
	/**
	 * 上班时长（分钟）
	 * ** 上班总时长为 上班时长（小时）+ 上班时长（分钟） 算上午休时间
	 */
	private static final Integer ON_DUTY_LASTING_MIN_SETTING = 30;


	private static final Integer GLOBAL_FONT_SIZE = 15;

	public OffWorkClock() {
		initMainPage();

		// 其他初始化代码
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenuItem openItem = new JMenuItem("首页    ✅");
		JMenuItem saveItem = new JMenuItem("配置页");
		fileMenu.add(openItem);
		fileMenu.add(saveItem);
		menuBar.add(fileMenu);
		setJMenuBar(menuBar);

		openItem.addActionListener(e -> {
			removeAll();
			initMainPage();
			revalidate();
			repaint();
			openItem.setText("首页    ✅");
			saveItem.setText("配置页");
			setSize(600, 150);
		});
		saveItem.addActionListener(e -> {
			removeAll();
			initConfigPage();
			revalidate();
			repaint();
			openItem.setText("首页");
			saveItem.setText("配置页  ✅");
			setSize(900, 150);
		});

	}

	public void removeAll() {
		if(timeLabel0 != null) remove(timeLabel0);
		if(timeLabel != null) remove(timeLabel);
		if(timeLabel1 != null) remove(timeLabel1);
		if(settingLabel1 != null) remove(settingLabel1);
		if(settingLabel2 != null) remove(settingLabel2);
		if(startButton != null) remove(startButton);
		if(settingButton != null) remove(settingButton);
		if(textArea != null) remove(textArea);
		if(settingTextArea1 != null) remove(settingTextArea1);
		if(settingTextArea2 != null) remove(settingTextArea2);
	}


	public void initConfigPage() {
		setTitle("准时下班");
		setSize(600, 150);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new FlowLayout());

		settingLabel1 = new JLabel();
		settingLabel1.setFont(new Font("Arial", Font.PLAIN, GLOBAL_FONT_SIZE));
		settingLabel1.setText("灵活上班开始时间 ：");
		add(settingLabel1);

		settingTextArea1 = new JTextArea();
		settingTextArea1.setEditable(true);
		settingTextArea1.setFont(new Font("Monospaced", Font.PLAIN, GLOBAL_FONT_SIZE));
		settingTextArea1.setPreferredSize(new Dimension(250, GLOBAL_FONT_SIZE)); // 设置首选宽度为200像素，高度为100像素
		settingTextArea1.setText(ON_DUTY_START_SETTING_STR);
		add(settingTextArea1);

		settingLabel2 = new JLabel();
		settingLabel2.setFont(new Font("Arial", Font.PLAIN, GLOBAL_FONT_SIZE));
		settingLabel2.setText("灵活上班结束时间：");
		add(settingLabel2);

		settingTextArea2 = new JTextArea();
		settingTextArea2.setEditable(true);
		settingTextArea2.setFont(new Font("Monospaced", Font.PLAIN, GLOBAL_FONT_SIZE));
		settingTextArea2.setPreferredSize(new Dimension(250, GLOBAL_FONT_SIZE)); // 设置首选宽度为200像素，高度为100像素
		settingTextArea2.setText(ON_DUTY_END_SETTING_STR); // 设置首选宽度为200像素，高度为100像素
		add(settingTextArea2);

		settingButton = new JButton();
		settingButton.setFont(new Font("Arial", Font.PLAIN, GLOBAL_FONT_SIZE));
		settingButton.setText("Save");
		settingButton.addActionListener(e -> {
			settingButton.setText("Loading");
			String startWorkingFrom = settingTextArea1.getText();
			String startWorkingTo = settingTextArea2.getText();
			if (checkTimeFormat(startWorkingFrom.isEmpty(), "还没设置灵活上班开始时间")) return;
			if (checkTimeFormat(startWorkingTo.isEmpty(), "还没设置灵活上班结束时间")) return;
			if (checkTimeFormat(!DateUtils.isTimeFormat(startWorkingFrom, TIME_FORMAT), "灵活上班开始时间配置格式为 " + TIME_FORMAT)) return;
			if (checkTimeFormat(!DateUtils.isTimeFormat(startWorkingTo, TIME_FORMAT), "灵活上班结束时间配置格式为 " + TIME_FORMAT)) return;
			ON_DUTY_START_SETTING_STR = startWorkingFrom;
			ON_DUTY_END_SETTING_STR = startWorkingTo;
			settingButton.setText("Save success");
			try {
				Thread.sleep(2);
			} catch (InterruptedException ex) {
				throw new RuntimeException(ex);
			}
			settingButton.setText("Save");
//			timer.stop();
			startButton.setText("Start");
		});
		add(settingButton);
	}

	public void initMainPage() {
		setTitle("准时下班");
		setSize(600, 150);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new FlowLayout());

		timeLabel0 = new JLabel();
		timeLabel0.setFont(new Font("Arial", Font.PLAIN, GLOBAL_FONT_SIZE));
		timeLabel0.setText("上班时间：");
		add(timeLabel0);

		textArea = new JTextArea();
		textArea.setEditable(true);
		textArea.setFont(new Font("Monospaced", Font.PLAIN, GLOBAL_FONT_SIZE));
		textArea.setPreferredSize(new Dimension(250, GLOBAL_FONT_SIZE)); // 设置首选宽度为200像素，高度为100像素
		add(textArea);

		startButton = new JButton();
		startButton.setFont(new Font("Arial", Font.PLAIN, GLOBAL_FONT_SIZE));
		startButton.setText("Start");
		startButton.addActionListener(e -> {
			timer = new Timer(1000, new TimeListener());
			timer.start();
			startButton.setText("Loading");
		});
		add(startButton);

		timeLabel = new JLabel();
		timeLabel.setFont(new Font("Arial", Font.PLAIN, GLOBAL_FONT_SIZE));
		add(timeLabel);

		timeLabel1 = new JLabel();
		timeLabel1.setFont(new Font("Arial", Font.PLAIN, GLOBAL_FONT_SIZE));
		add(timeLabel1);
	}

	private class TimeListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String text = textArea.getText();
			if (checkTimeFormat(text.isEmpty(), "还没设置上班时间")) return;
			if (checkTimeFormat(!DateUtils.isTimeFormat(text, TIME_FORMAT), "上班时间配置格式为 " + TIME_FORMAT)) return;

			Date startWorkDate = DateUtils.stringToDate(TODAY + text, DateUtils.DATE_TIME_PATTERN);
			if (!DateUtils.checkTimeBetween(startWorkDate, ON_DUTY_START_SETTING, ON_DUTY_END_SETTING)
				&& startWorkDate.after(ON_DUTY_END_SETTING)
			) {
				startWorkDate = DateUtils.stringToDate(ON_DUTY_END_SETTING_STR, TIME_FORMAT);
			} else if (!DateUtils.checkTimeBetween(startWorkDate, ON_DUTY_START_SETTING, ON_DUTY_END_SETTING)
					&& startWorkDate.before(ON_DUTY_START_SETTING)
			){
				startWorkDate = DateUtils.stringToDate(ON_DUTY_START_SETTING_STR, TIME_FORMAT);
			}
			Date endWorkDate = DateUtils.addDateHours(startWorkDate, ON_DUTY_LASTING_HOUR_SETTING);
			endWorkDate = DateUtils.addDateMinutes(endWorkDate, ON_DUTY_LASTING_MIN_SETTING);
			String endWorkTime = DateUtils.format(endWorkDate, TIME_FORMAT);



			Date nowDate = new Date();
			long nowTs = nowDate.getTime();
			long endWorkTs = endWorkDate.getTime();
			Interval interval;
			if (nowTs > endWorkTs) {
				interval = new Interval(endWorkTs, nowTs);
				Period p = interval.toPeriod();
				timeLabel.setText("今天下班已经过了：" + p.getHours() + " h " + p.getMinutes() + " min " + p.getSeconds() + " s ");
			} else {
				interval = new Interval(nowTs, endWorkTs);
				Period p = interval.toPeriod();
				timeLabel.setText("今天离下班时间还有：" + p.getHours() + " h " + p.getMinutes() + " min " + p.getSeconds() + " s ");
			}

			timeLabel1.setText("今天可以下班的时间是：" + endWorkTime);
			startButton.setText("Fresh");

			System.out.println(getSize());
		}
	}

	private boolean checkTimeFormat(boolean text, String TIME_FORMAT) {
		if (text) {
			timeLabel.setText(TIME_FORMAT);
			timeLabel1.setText("                              ");
			startButton.setText("Start");
			return true;
		}
		return false;
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			// 设置菜单栏显示在屏幕顶部
			System.setProperty("apple.laf.useScreenMenuBar", "true");
			System.setProperty("apple.awt.application.name", "准时下班");

			OffWorkClock clock = new OffWorkClock();
			clock.setVisible(true);
		});
	}


}

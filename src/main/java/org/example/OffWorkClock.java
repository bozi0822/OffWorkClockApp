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

	private final JLabel timeLabel0;
	private final JLabel timeLabel;
	private final JLabel timeLabel1;
	private final JButton startButton;
	private final JTextArea textArea;
	private Timer timer;

	private static final String TODAY = LocalDateTime.now().toString(DateUtils.DATE_PATTERN) + " ";
	private static final String TIME_FORMAT = "HH:mm:ss";

	/**
	 * 灵活上班时间时间： 8：30 - 8：:45
	 */
	private static final String ON_DUTY_START_SETTING_STR = "08:30:00";
	private static final String ON_DUTY_END_SETTING_STR = "08:45:00";

	private static final Date ON_DUTY_START_SETTING = DateUtils.stringToDate(TODAY + ON_DUTY_START_SETTING_STR, DateUtils.DATE_TIME_PATTERN);
	private static final Date ON_DUTY_END_SETTING = DateUtils.stringToDate(TODAY + ON_DUTY_END_SETTING_STR, DateUtils.DATE_TIME_PATTERN);

	/**
	 * 上班时长（小时）
	 */
	private static final Integer ON_DUTY_LASTING_HOUR_SETTING = 8;
	/**
	 * 上班时长（分钟）
	 * ** 上班总时长为 上班时长（小时）+ 上班时长（分钟）
	 */
	private static final Integer ON_DUTY_LASTING_MIN_SETTING = 30;


	private static final Integer GLOBAL_FONT_SIZE = 15;

	public OffWorkClock() {
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
			if (text.isEmpty()) {
				timeLabel.setText("还没设置上班时间");
				timeLabel1.setText("                              ");
				startButton.setText("Start");
				return;
			}
			if (!DateUtils.isTimeFormat(text, TIME_FORMAT)) {
				timeLabel.setText("上班时间配置格式为 " + TIME_FORMAT);
				timeLabel1.setText("                              ");
				startButton.setText("Start");
				return;
			}

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
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			OffWorkClock clock = new OffWorkClock();
			clock.setVisible(true);
		});
	}


}

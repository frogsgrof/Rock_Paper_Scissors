public class RockPaperScissorsRunner {

    public static void main(String[] args) {

        RockPaperScissorsFrame frame = new RockPaperScissorsFrame();
        frame.setVisible(true);

        /*
        Requests focus to the text area inside the frame. This is to stop the "rock" button from automatically
        getting focus on start, which gives it this annoying highlighted border. The text area doesn't get focus
        painted, so it can be focused without that being visible to the user.
        */
        frame.resultsTA.requestFocusInWindow();
    }
}
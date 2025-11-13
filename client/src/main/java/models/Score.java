package models;

public class Score {

    private int rank;
    private String playerName;
    private int points;
    private int streak;
    private String medal;
    private String streakDisplay;

    public Score() {
    }

    public Score(String playerName, int points) {
        this.playerName = playerName;
        this.points = points;
        this.streak = 0;
        this.rank = 0;
        updateMedalAndStreak();
    }

    public Score(int rank, String playerName, int points, int streak) {
        this.rank = rank;
        this.playerName = playerName;
        this.points = points;
        this.streak = streak;
        updateMedalAndStreak();
    }

    private void updateMedalAndStreak() {
        // Set medal based on rank
        if (rank == 1) this.medal = "🥇";
        else if (rank == 2) this.medal = "🥈";
        else if (rank == 3) this.medal = "🥉";
        else this.medal = String.valueOf(rank);

        // Format streak display
        this.streakDisplay = streak > 0 ? "🔥 " + streak : "-";
    }

    // Getters and Setters
    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
        updateMedalAndStreak();
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getStreak() {
        return streak;
    }

    public void setStreak(int streak) {
        this.streak = streak;
        updateMedalAndStreak();
    }

    public String getMedal() {
        return medal;
    }

    public String getStreakDisplay() {
        return streakDisplay;
    }
}

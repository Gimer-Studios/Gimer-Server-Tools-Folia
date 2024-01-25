package me.liam.operator_tools;

import java.io.Serializable;

public class PlayerStats implements Serializable {
    private int kills;
    private int deaths;

    public PlayerStats(int kills, int deaths) {
        this.kills = kills;
        this.deaths = deaths;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }
}

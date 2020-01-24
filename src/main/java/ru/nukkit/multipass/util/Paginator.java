/*
    Multipass, Nukkit permissions plugin
    Copyright (C) 2016  fromgate, nukkit.ru

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ru.nukkit.multipass.util;

import cn.nukkit.command.CommandSender;
import cn.nukkit.player.Player;

import java.util.List;

public class Paginator {

    public static void printPage(CommandSender p, List<String> ln, String title, String footer, String emptyMsg, int pageNum, int lpp, boolean shownum) {
        Paginator pg = new Paginator(ln, title, footer, emptyMsg, lpp, shownum);
        pg.printPage(p, pageNum, lpp > 0 ? lpp : (p instanceof Player ? 5 : 1000));
    }

    public static void printPage(CommandSender p, List<String> ln, String title, int pageNum) {
        Paginator pg = new Paginator(ln);
        pg.setTitle(title);
        pg.setLinePerPage(p instanceof Player ? 5 : 1000);
        pg.printPage(p, pageNum);
    }

    private List<String> ln;
    private int lpp;
    private String title;
    private String footer;
    private String emptyList;
    private boolean shownum;

    public void setLinePerPage(int lpp) {
        this.lpp = lpp;
    }

    public Paginator(List<String> ln) {
        this.ln = ln;
        this.lpp = 5;
        this.title = "List";
        this.footer = "Page: %1% / %2%";
        this.emptyList = "List is empty";
        this.shownum = false;

    }

    public Paginator(List<String> ln, String title, String footer, String emptyMsg, int lpp, boolean shownum) {
        this(ln);
        if (lpp > 0) this.lpp = lpp;
        if (title != null && !title.isEmpty()) this.title = title;
        if (footer != null && !footer.isEmpty()) this.footer = footer;
        if (emptyMsg != null && !emptyMsg.isEmpty()) this.emptyList = emptyMsg;
        this.shownum = shownum;
    }

    public void addLine(String str) {
        ln.add(str);
    }

    public boolean isEmpty() {
        return ln.isEmpty();
    }

    public void setTitle(String title_msgid) {
        this.title = title_msgid;

    }

    public void setShowNum(boolean shownum) {
    }

    public void setFooter(String footer_msgid) {
        this.footer = footer_msgid;
    }

    public void printPage(CommandSender p, int pnum) {
        printPage(p, pnum, this.lpp);
    }

    public void printPage(CommandSender p, int pnum, int linesperpage) {
        if (ln.size() > 0) {

            int maxp = ln.size() / linesperpage;
            if ((ln.size() % linesperpage) > 0) maxp++;
            if (pnum > maxp) pnum = maxp;
            int maxl = linesperpage;
            if (pnum == maxp) {
                maxl = ln.size() % linesperpage;
                if (maxp == 1) maxl = ln.size();
            }
            if (maxl == 0) maxl = linesperpage;


            printMsg(p, title);

            String numpx = "";
            for (int i = 0; i < maxl; i++) {
                if (shownum) numpx = "&3" + Integer.toString(1 + i + (pnum - 1) * linesperpage) + ". ";
                printMsg(p, numpx + "&a" + ln.get(i + (pnum - 1) * linesperpage));
            }
            if (maxp > 1)
                printMsg(p, this.footer.replaceAll("%1%", String.valueOf(pnum)).replaceAll("%2%", String.valueOf(maxp)));
        } else printMsg(p, this.emptyList);
    }

    public void printMsg(CommandSender p, String msg) {
        if (p != null && msg != null) p.sendMessage(colorize(msg));
    }

    public static String colorize(String textToTranslate) {
        char[] b = textToTranslate.toCharArray();
        for (int i = 0; i < b.length - 1; i++) {
            if ((b[i] == '&') && ("0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[(i + 1)]) > -1)) {
                b[i] = '§';
                b[(i + 1)] = Character.toLowerCase(b[(i + 1)]);
            }
        }
        return new String(b);
    }
}

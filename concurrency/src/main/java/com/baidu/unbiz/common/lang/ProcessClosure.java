/**
 * 
 */
package com.baidu.unbiz.common.lang;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.baidu.unbiz.common.able.Processable;
import com.baidu.unbiz.common.logger.CachedLogger;

/**
 * 
 * @author <a href="mailto:xuchen06@baidu.com">xuc</a>
 * @version create on 2014年7月31日 下午8:29:32
 */
public class ProcessClosure extends CachedLogger implements Processable {

    private String info;

    @Override
    public void execute(Object...input) {

        for (Object cmd : input) {
            if (cmd.getClass().isArray()) {
                this.execute(object2Array(cmd));
                continue;
            }
            this.execute(cmd.toString());
        }
    }

    public void execute(String...input) {
        work(input);
    }

    @Override
    public void execute(String input) {
        work(input);
    }

    private void work(String...cmds) {
        StringBuilder builder = new StringBuilder();
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(cmds);
            InputStreamReader isr = new InputStreamReader(process.getInputStream());
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                logger.info(line);
                builder.append(line).append("\n");
            }

        } catch (IOException e) {
            builder.append(e);
            logger.error("execute process error", e);
        } finally {
            if (process != null) {
                process.destroy();
            }
            info = builder.toString();
        }
    }

    private String[] object2Array(Object obj) {
        Object[] cmds = (Object[]) obj;
        String[] result = new String[cmds.length];
        for (int i = 0; i < cmds.length; i++) {
            result[i] = cmds[i].toString();
        }
        return result;
    }

    public String getInfo() {
        return info;
    }

}

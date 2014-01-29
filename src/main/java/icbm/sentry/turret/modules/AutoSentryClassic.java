package icbm.sentry.turret.modules;

import icbm.sentry.turret.tiles.TileSentry;

public class AutoSentryClassic extends AutoSentry
{
    public AutoSentryClassic(TileSentry host)
    {
        super(host);
        this.maxHealth = 200;
        this.centerOffset.y = 0.65;
    }

    @Override
    public Modules getSentryType ()
    {
        return Modules.CLASSIC;
    }
    
}

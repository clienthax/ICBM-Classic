package icbm.classic.content.explosive.blast;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.List;

public class BlastMutation extends Blast
{
    public BlastMutation(World world, Entity entity, double x, double y, double z, float size)
    {
        super(world, entity, x, y, z, size);
    }

    @Override
    public void doExplode()
    {

        if (!this.world().isRemote)
        {
            AxisAlignedBB bounds = new AxisAlignedBB(location.x() - this.getBlastRadius(), location.y() - this.getBlastRadius(), location.z() - this.getBlastRadius(), location.x() + this.getBlastRadius(), location.y() + this.getBlastRadius(), location.z() + this.getBlastRadius());
            List<EntityLiving> entitiesNearby = world().getEntitiesWithinAABB(EntityLiving.class, bounds);

            for (EntityLiving entity : entitiesNearby)
            {
                if (entity instanceof EntityPig)
                {
                    EntityPigZombie newEntity = new EntityPigZombie(world());
                    newEntity.preventEntitySpawning = true;
                    newEntity.setPosition(entity.posX, entity.posY, entity.posZ);
                    entity.setDead();
                }
                else if (entity instanceof EntityVillager)
                {
                    EntityZombie newEntity = new EntityZombie(world());
                    newEntity.preventEntitySpawning = true;
                    newEntity.setPosition(entity.posX, entity.posY, entity.posZ);
                    entity.setDead();
                }
            }
        }
    }

    @Override
    public float getBlastRadius()
    {
        return 0;
    }
}

package pixlepix.auracascade.block.tile;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.AxisAlignedBB;
import pixlepix.auracascade.data.AuraQuantity;
import pixlepix.auracascade.data.EnumAura;
import pixlepix.auracascade.main.AuraUtil;

import java.util.List;

/**
 * Created by pixlepix on 11/29/14.
 */
public class AuraTilePump extends AuraTile {

    public int pumpPower;

    @Override
    protected void readCustomNBT(NBTTagCompound nbt) {
        super.readCustomNBT(nbt);
        pumpPower = nbt.getInteger("pumpPower");
    }

    @Override
    protected void writeCustomNBT(NBTTagCompound nbt) {
        super.writeCustomNBT(nbt);
        nbt.setInteger("pumpPower", pumpPower);
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if(worldObj.getTotalWorldTime() % 2400 == 0){
            AuraUtil.keepAlive(this, 3);
        }
        if(!worldObj.isRemote && worldObj.getTotalWorldTime() % 20 ==2){
            if(pumpPower != 0){
                AuraTile upNode = null;
                for(int i=1; i<15; i++){
                    TileEntity te = worldObj.getTileEntity(xCoord, yCoord+i, zCoord);
                    if(te instanceof AuraTile){
                        upNode = (AuraTile) te;
                        break;
                    }
                }
                if(upNode != null){
                    pumpPower--;
                    int dist = upNode.yCoord - yCoord;
                    int quantity = 500/dist;
                    quantity = Math.min(quantity, storage.get(EnumAura.WHITE_AURA));
                    storage.subtract(EnumAura.WHITE_AURA , quantity);
                    upNode.storage.add(new AuraQuantity(EnumAura.WHITE_AURA, quantity));
                }
            }else{
                int range = 3;
                List<EntityItem> nearbyItems = worldObj.getEntitiesWithinAABB(EntityItem.class, AxisAlignedBB.getBoundingBox(xCoord - range, yCoord - range, zCoord - range, xCoord + range, yCoord + range, zCoord + range));
                for (EntityItem entityItem : nearbyItems) {
                    ItemStack stack = entityItem.getEntityItem();
                    if(TileEntityFurnace.getItemBurnTime(stack) != 0){
                        pumpPower = TileEntityFurnace.getItemBurnTime(stack);

                        //Kill the stack
                        if (stack.stackSize == 0) {
                            entityItem.setDead();
                        } else {
                            stack.stackSize--;
                        }

                        break;
                    }
                }
            }
        }
    }


}
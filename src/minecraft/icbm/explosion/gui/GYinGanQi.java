package icbm.explosion.gui;

import icbm.api.ICBM;
import icbm.explosion.ZhuYaoExplosion;
import icbm.explosion.jiqi.TYinGanQi;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;

import org.lwjgl.opengl.GL11;

import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.network.PacketManager;
import cpw.mods.fml.common.network.PacketDispatcher;

public class GYinGanQi extends ICBMGui
{
	private TYinGanQi tileEntity;

	private int containerWidth;
	private int containerHeight;

	private GuiTextField textFieldFreq;

	private GuiTextField textFieldminX;
	private GuiTextField textFieldminY;
	private GuiTextField textFieldminZ;

	private GuiTextField textFieldmaxX;
	private GuiTextField textFieldmaxY;
	private GuiTextField textFieldmaxZ;

	public GYinGanQi(TYinGanQi tileEntity)
	{
		this.tileEntity = tileEntity;
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	@Override
	public void initGui()
	{
		super.initGui();

		this.controlList.clear();

		String mode = "All";

		if (this.tileEntity.mode == 1)
		{
			mode = "Players";
		}
		else if (this.tileEntity.mode == 2)
		{
			mode = "Mobs";
		}

		this.controlList.add(new GuiButton(0, this.width / 2 - 15, this.height / 2 + 32, 45, 20, mode));

		this.textFieldFreq = new GuiTextField(fontRenderer, 75, 100, 40, 12);
		this.textFieldFreq.setMaxStringLength(4);
		this.textFieldFreq.setText(this.tileEntity.frequency + "");

		// Min
		this.textFieldminX = new GuiTextField(fontRenderer, 75, 50, 20, 12);
		this.textFieldminX.setMaxStringLength(2);
		this.textFieldminX.setText(this.tileEntity.minCoord.intX() + "");

		this.textFieldminY = new GuiTextField(fontRenderer, 75, 67, 20, 12);
		this.textFieldminY.setMaxStringLength(2);
		this.textFieldminY.setText(this.tileEntity.minCoord.intY() + "");

		this.textFieldminZ = new GuiTextField(fontRenderer, 75, 82, 20, 12);
		this.textFieldminZ.setMaxStringLength(2);
		this.textFieldminZ.setText(this.tileEntity.minCoord.intZ() + "");

		// Max
		this.textFieldmaxX = new GuiTextField(fontRenderer, 130, 50, 20, 12);
		this.textFieldmaxX.setMaxStringLength(2);
		this.textFieldmaxX.setText(this.tileEntity.maxCoord.intX() + "");

		this.textFieldmaxY = new GuiTextField(fontRenderer, 130, 67, 20, 12);
		this.textFieldmaxY.setMaxStringLength(2);
		this.textFieldmaxY.setText(this.tileEntity.maxCoord.intY() + "");

		this.textFieldmaxZ = new GuiTextField(fontRenderer, 130, 82, 20, 12);
		this.textFieldmaxZ.setMaxStringLength(2);
		this.textFieldmaxZ.setText(this.tileEntity.maxCoord.intZ() + "");

		PacketDispatcher.sendPacketToServer(PacketManager.getPacket(ZhuYaoExplosion.CHANNEL, this.tileEntity, (int) -1, true));

	}

	@Override
	public void onGuiClosed()
	{
		super.onGuiClosed();
		PacketDispatcher.sendPacketToServer(PacketManager.getPacket(ZhuYaoExplosion.CHANNEL, this.tileEntity, (int) -1, false));

	}

	/**
	 * Fired when a control is clicked. This is the equivalent of
	 * ActionListener.actionPerformed(ActionEvent e).
	 */
	@Override
	protected void actionPerformed(GuiButton par1GuiButton)
	{
		if (par1GuiButton.id == 0)
		{
			this.tileEntity.mode++;

			if (this.tileEntity.mode > 2)
			{
				this.tileEntity.mode = 0;
			}

			PacketDispatcher.sendPacketToServer(PacketManager.getPacket(ZhuYaoExplosion.CHANNEL, this.tileEntity, (int) 2, this.tileEntity.mode));
		}
	}

	/**
	 * Call this method from you GuiScreen to process the keys into textbox.
	 */
	@Override
	public void keyTyped(char par1, int par2)
	{
		super.keyTyped(par1, par2);
		this.textFieldminX.textboxKeyTyped(par1, par2);
		this.textFieldminY.textboxKeyTyped(par1, par2);
		this.textFieldminZ.textboxKeyTyped(par1, par2);
		this.textFieldmaxX.textboxKeyTyped(par1, par2);
		this.textFieldmaxY.textboxKeyTyped(par1, par2);
		this.textFieldmaxZ.textboxKeyTyped(par1, par2);
		this.textFieldFreq.textboxKeyTyped(par1, par2);

		try
		{
			Vector3 newMinCoord = new Vector3(Integer.parseInt(this.textFieldminX.getText()), Integer.parseInt(this.textFieldminY.getText()), Integer.parseInt(this.textFieldminZ.getText()));

			this.tileEntity.minCoord = newMinCoord;
			PacketDispatcher.sendPacketToServer(PacketManager.getPacket(ZhuYaoExplosion.CHANNEL, this.tileEntity, (int) 4, this.tileEntity.minCoord.intX(), this.tileEntity.minCoord.intY(), this.tileEntity.minCoord.intZ()));
		}
		catch (Exception e)
		{
		}

		try
		{
			Vector3 newMaxCoord = new Vector3(Integer.parseInt(this.textFieldmaxX.getText()), Integer.parseInt(this.textFieldmaxY.getText()), Integer.parseInt(this.textFieldmaxZ.getText()));

			this.tileEntity.maxCoord = newMaxCoord;
			PacketDispatcher.sendPacketToServer(PacketManager.getPacket(ZhuYaoExplosion.CHANNEL, this.tileEntity, (int) 5, this.tileEntity.maxCoord.intX(), this.tileEntity.maxCoord.intY(), this.tileEntity.maxCoord.intZ()));
		}
		catch (Exception e)
		{

		}

		try
		{
			short newFrequency = (short) Math.max(0, Short.parseShort(this.textFieldFreq.getText()));

			this.tileEntity.frequency = newFrequency;
			PacketDispatcher.sendPacketToServer(PacketManager.getPacket(ZhuYaoExplosion.CHANNEL, this.tileEntity, (int) 3, this.tileEntity.frequency));
		}
		catch (Exception e)
		{

		}
	}

	/**
	 * Args: x, y, buttonClicked
	 */
	@Override
	public void mouseClicked(int par1, int par2, int par3)
	{
		super.mouseClicked(par1, par2, par3);
		this.textFieldminX.mouseClicked(par1 - containerWidth, par2 - containerHeight, par3);
		this.textFieldminY.mouseClicked(par1 - containerWidth, par2 - containerHeight, par3);
		this.textFieldminZ.mouseClicked(par1 - containerWidth, par2 - containerHeight, par3);
		this.textFieldmaxX.mouseClicked(par1 - containerWidth, par2 - containerHeight, par3);
		this.textFieldmaxY.mouseClicked(par1 - containerWidth, par2 - containerHeight, par3);
		this.textFieldmaxZ.mouseClicked(par1 - containerWidth, par2 - containerHeight, par3);
		this.textFieldFreq.mouseClicked(par1 - containerWidth, par2 - containerHeight, par3);
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawForegroundLayer()
	{
		this.fontRenderer.drawString("Proximity Detector", 48, 6, 4210752);

		this.fontRenderer.drawString("Detection Range", 12, 25, 4210752);

		this.fontRenderer.drawString("Min", 75, 40, 4210752);
		this.fontRenderer.drawString("Max", 130, 40, 4210752);

		this.fontRenderer.drawString("X-Coord:", 15, 51, 4210752);
		this.fontRenderer.drawString("Y-Coord:", 15, 68, 4210752);
		this.fontRenderer.drawString("Z-Coord:", 15, 83, 4210752);

		this.textFieldminX.drawTextBox();
		this.textFieldminY.drawTextBox();
		this.textFieldminZ.drawTextBox();

		this.textFieldmaxX.drawTextBox();
		this.textFieldmaxY.drawTextBox();
		this.textFieldmaxZ.drawTextBox();

		this.fontRenderer.drawString("Frequency:", 15, 102, 4210752);

		if (this.tileEntity.isInverted)
		{
			this.fontRenderer.drawString("Exclude", 120, 102, 4210752);
		}
		else
		{
			this.fontRenderer.drawString("Include", 120, 102, 4210752);
		}
		this.fontRenderer.drawString("Target:", 15, 120, 4210752);

		this.textFieldFreq.drawTextBox();

		String color = "\u00a74";
		String status = "Idle";

		if (this.tileEntity.isDisabled())
		{
			status = "Disabled";
		}
		else if (this.tileEntity.prevWatts < this.tileEntity.getRequest().getWatts())
		{
			status = "Insufficient electricity!";
		}
		else
		{
			color = "\u00a72";
			status = "On";
		}

		this.fontRenderer.drawString(color + "Status: " + status, 12, 138, 4210752);
		this.fontRenderer.drawString("Voltage: " + this.tileEntity.getVoltage() + "v", 12, 150, 4210752);
	}

	/**
	 * Draw the background layer for the GuiContainer (everything behind the items)
	 */
	@Override
	protected void drawBackgroundLayer(float par1, int par2, int par3)
	{
		int var4 = this.mc.renderEngine.getTexture(ICBM.TEXTURE_FILE_PATH + "EmptyGUI.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(var4);
		containerWidth = (this.width - this.xSize) / 2;
		containerHeight = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(containerWidth, containerHeight, 0, 0, this.xSize, this.ySize);
	}

	@Override
	public void updateScreen()
	{
		super.updateScreen();

		String mode = "All";

		if (this.tileEntity.mode == 1)
		{
			mode = "Players";
		}
		else if (this.tileEntity.mode == 2)
		{
			mode = "Mobs";
		}

		((GuiButton) this.controlList.get(0)).displayString = mode;

		if (!this.textFieldminX.isFocused())
			this.textFieldminX.setText(this.tileEntity.minCoord.intX() + "");
		if (!this.textFieldminY.isFocused())
			this.textFieldminY.setText(this.tileEntity.minCoord.intY() + "");
		if (!this.textFieldminZ.isFocused())
			this.textFieldminZ.setText(this.tileEntity.minCoord.intZ() + "");

		if (!this.textFieldmaxX.isFocused())
			this.textFieldmaxX.setText(this.tileEntity.maxCoord.intX() + "");
		if (!this.textFieldmaxY.isFocused())
			this.textFieldmaxY.setText(this.tileEntity.maxCoord.intY() + "");
		if (!this.textFieldmaxZ.isFocused())
			this.textFieldmaxZ.setText(this.tileEntity.maxCoord.intZ() + "");

		if (!this.textFieldFreq.isFocused())
			this.textFieldFreq.setText(this.tileEntity.frequency + "");
	}
}

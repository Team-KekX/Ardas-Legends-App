const {capitalizeFirstLetters, isMemberStaff} = require("../../../../utils/utilities");
const {MessageEmbed} = require("discord.js");
const {ADMIN} = require("../../../../configs/embed_thumbnails.json");


module.exports = {
    async execute(interaction) {
        if (!isMemberStaff(interaction)) {
            await interaction.reply({content: "You don't have permission to use this command.", ephemeral: true});
            return;
        }
        const characterName = capitalizeFirstLetters(interaction.options.getString('character-name').toLowerCase());
        // send to server
        const replyEmbed = new MessageEmbed()
            .setTitle(`Delete character`)
            .setColor('NAVY')
            .setDescription(`Deleted character ${characterName}.`)
            .setThumbnail(ADMIN)
            .setTimestamp()
        await interaction.reply({embeds: [replyEmbed]});
    },
};


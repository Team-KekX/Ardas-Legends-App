const {isMemberStaff} = require("../../../../utils/utilities");
const {MessageEmbed} = require("discord.js");
const {ADMIN} = require("../../../../configs/embed_thumbnails.json");
const {serverIP, serverPort} = require("../../../configs/config.json");


module.exports = {
    async execute(interaction) {
        if (!isMemberStaff(interaction)) {
            await interaction.reply({content: "You don't have permission to use this command.", ephemeral: true});
            return;
        }
        const discId = interaction.options.getString('discord-id');
        // send to server
        const data = {
            discordID: discId,
        }

        axios.post('http://'+serverIP+':'+serverPort+'/api/player/delete', data)
            .then(async function (response) {
                // The request and data is successful.
                const replyEmbed = new MessageEmbed()
                    .setTitle(`Delete player`)
                    .setColor('NAVY')
                    .setDescription(`Deleted player with discord ID: ${discordId}.`)
                    .setThumbnail(ADMIN)
                    .setTimestamp()
                await interaction.reply({embeds: [replyEmbed]});
            })
            .catch(async function (error) {
                // An error occurred during the request.
                await interaction.reply({content: `${error.response.data.message}`, ephemeral: true});
            })
        
    }
};
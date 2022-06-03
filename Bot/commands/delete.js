const {SlashCommandBuilder} = require("@discordjs/builders");
const fs = require("fs");

module.exports = {
    data: new SlashCommandBuilder()
        .setName('delete')
        .setDescription('Deletes an entity')
        .addSubcommand(subcommand =>
            subcommand
                .setName('player')
                .setDescription('Deletes a player')
                .addStringOption(option =>
                    option.setName('discord-id')
                        .setDescription('The Discord ID of the player to delete')
                        .setRequired(true))
        )
        .addSubcommand(subcommand =>
            subcommand
                .setName('character')
                .setDescription('Deletes a character')
                .addStringOption(option =>
                    option.setName('character-name')
                        .setDescription('The name of the character to delete')
                        .setRequired(true))
        ),
    async execute(interaction) {
        // Dynamically get all subcommands for called command
        const commands = {};
        const path = './Bot/commands/subcommands/admin/delete/';
        const files = fs.readdirSync(path, (err, tmp_files) => tmp_files.filter(file => file.contains('delete_')));
        for (const file of files) {
            const name = file.split('delete_')[1].slice(0, -3);
            commands[name] = require('./subcommands/admin/delete/' + file);
        }
        const toExecute = commands[interaction.options.getSubcommand()];
        toExecute.execute(interaction);
    },
};

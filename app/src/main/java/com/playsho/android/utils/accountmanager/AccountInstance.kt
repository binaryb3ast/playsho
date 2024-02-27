package com.playsho.android.utils.accountmanager

import com.playsho.android.base.ApplicationLoader

import android.accounts.Account
import android.accounts.AccountManager
import android.os.Bundle
/**
 * The `AccountInstance` class provides utility methods for managing user accounts
 * using the Android `AccountManager`.
 */
object AccountInstance {

    private val ACCOUNT_TYPE = ApplicationLoader.context.packageName
    private val accountManager: AccountManager = AccountManager.get(ApplicationLoader.context)
    private var currentAccount: Account? = null

    /**
     * Set the current active account.
     *
     * @param account The account to set as the current active account.
     */
    fun use(account: Account?) {
        currentAccount = account
    }

    /**
     * Get the current active account.
     *
     * @return The current active account.
     */
    fun getCurrentAccount(): Account? {
        return currentAccount
    }

    /**
     * Check if there is any account available.
     *
     * @return `true` if there is at least one account, otherwise `false`.
     */
    fun hasAnyAccount(): Boolean {
        return getAccounts().isNotEmpty()
    }

    /**
     * Update user data associated with a specific account.
     *
     * @param account The account to update user data for.
     * @param key     The key for the user data.
     * @param value   The new value for the user data.
     */
    fun updateAccountUserData(account: Account, key: String, value: String) {
        accountManager.setUserData(account, key, value)
    }

    /**
     * Update user data associated with the current active account.
     *
     * @param key   The key for the user data.
     * @param value The new value for the user data.
     */
    fun updateAccountUserData(key: String, value: String) {
        currentAccount?.let { accountManager.setUserData(it, key, value) }
    }

    /**
     * Get user data associated with a specific account.
     *
     * @param account The account to get user data for.
     * @param key     The key for the user data.
     * @return The value of the user data.
     */
    fun getUserData(account: Account, key: String): String? {
        return accountManager.getUserData(account, key)
    }

    /**
     * Get user data associated with the current active account.
     *
     * @param key The key for the user data.
     * @return The value of the user data.
     */
    fun getUserData(key: String): String? {
        return currentAccount?.let { accountManager.getUserData(it, key) }
    }

    /**
     * Get the authentication token for a specific account and token type.
     *
     * @param account    The account to get the authentication token for.
     * @param tokenType  The type of the authentication token.
     * @return The authentication token.
     */
    fun getAuthToken(account: Account, tokenType: String): String? {
        return accountManager.peekAuthToken(account, tokenType)
    }

    /**
     * Get the authentication token for the current active account and token type.
     *
     * @param tokenType The type of the authentication token.
     * @return The authentication token.
     */
    fun getAuthToken(tokenType: String): String {
        return currentAccount.let { accountManager.peekAuthToken(it, tokenType) }
    }

    /**
     * Initialize the `AccountInstance` by setting the first available account as the current active account.
     */
    fun init() {
        val accounts = getAccounts()
        if (accounts.isNotEmpty()) {
            use(accounts[0])
        }
    }

    /**
     * Get an array of all accounts of the specified type.
     *
     * @return An array of accounts.
     */
    fun getAccounts(): Array<Account> {
        return accountManager.getAccountsByType(ACCOUNT_TYPE)
    }

    /**
     * Remove an account by name.
     *
     * @param accountName The name of the account to remove.
     */
    fun removeAccount(accountName: String) {
        getAccounts().forEach { account ->
            if (account.name == accountName) {
                accountManager.removeAccountExplicitly(account)
                return@forEach
            }
        }
    }

    /**
     * Set the authentication token for a specific account and token type.
     *
     * @param account    The account to set the authentication token for.
     * @param tokenType  The type of the authentication token.
     * @param authToken  The authentication token.
     */
    fun setAuthToken(account: Account, tokenType: String, authToken: String) {
        accountManager.setAuthToken(account, tokenType, authToken)
    }

    /**
     * Set the authentication token for the current active account and token type.
     *
     * @param tokenType The type of the authentication token.
     * @param authToken The authentication token.
     */
    fun setAuthToken(tokenType: String, authToken: String) {
        currentAccount?.let { accountManager.setAuthToken(it, tokenType, authToken) }
    }

    /**
     * Create a new account with the specified name, password, and optional bundle.
     *
     * @param name   The name of the new account.
     * @param pass   The password for the new account.
     * @param bundle An optional bundle of account details.
     * @return The newly created account.
     */
    fun createAccount(name: String, pass: String, bundle: Bundle?): Account {
        val account = Account(name, ACCOUNT_TYPE)
        accountManager.addAccountExplicitly(account, pass, bundle)
        return account
    }
}

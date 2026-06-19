import React, { createContext, useContext, useState } from 'react';
import { Outlet } from 'react-router-dom';

const UsersContext = createContext(null);

export const useUsersContext = () => {
const context = useContext(UsersContext);

if (!context) {
throw new Error('useUsersContext must be used within a UsersProvider');
}

return context;
};

export const UsersLayout = () => {
const [usersData, setUsersData] = useState([]);
const [isCached, setIsCached] = useState(false);

return (
<UsersContext.Provider
value={{
usersData,
setUsersData,
isCached,
setIsCached
}}
> <Outlet />
</UsersContext.Provider>
);
};
